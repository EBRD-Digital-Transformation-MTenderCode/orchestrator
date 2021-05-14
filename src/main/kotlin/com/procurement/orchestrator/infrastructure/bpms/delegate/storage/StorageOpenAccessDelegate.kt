package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.domain.functional.validate
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponses
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.rule.duplicatesRule
import com.procurement.orchestrator.infrastructure.bpms.delegate.rule.missingEntityRule
import com.procurement.orchestrator.infrastructure.bpms.delegate.rule.unknownEntityRule
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.storage.StorageCommands
import com.procurement.orchestrator.infrastructure.client.web.storage.action.OpenAccessAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class StorageOpenAccessDelegate(
    logger: Logger,
    private val client: StorageClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<StorageOpenAccessDelegate.Parameters, OpenAccessAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        private const val DOCUMENT_ID_PATH = "document.id"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val entities: Map<EntityKey, EntityValue> = parameterContainer.getMapString("entities")
            .orForwardFail { fail -> return fail }
            .map { (key, value) ->
                val keyParsed = EntityKey.orNull(key)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "entities",
                            expectedValues = EntityKey.allowedElements.keysAsStrings(),
                            actualValue = key
                        )
                    )
                val valueParsed = EntityValue.orNull(value)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "entities",
                            expectedValues = EntityValue.allowedElements.keysAsStrings(),
                            actualValue = value
                        )
                    )
                keyParsed to valueParsed
            }.toMap()

        return success(Parameters(entities = entities))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<OpenAccessAction.Result>, Fail.Incident> {

        val documentIds = getAllDocuments(
            context.tender,
            context.awards,
            context.qualifications,
            context.submissions,
            context.parties,
            context.contracts,
            context.bids,
            parameters
        )
            .orForwardFail { fail -> return fail }

        if (documentIds.isEmpty())
            return success(Reply.None)

        val requestInfo = context.requestInfo
        return client.openAccess(
            id = commandId,
            params = OpenAccessAction.Params(
                datePublished = requestInfo.timestamp,
                ids = documentIds
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<OpenAccessAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.STORAGE, action = StorageCommands.OpenAccess)
            )

        val tender = context.tender
        val awards = context.awards
        val qualifications = context.qualifications
        val submissions = context.submissions
        val parties = context.parties
        val contracts = context.contracts
        val bids = context.bids

        validateData(
            data = data,
            tender = tender,
            awards = awards,
            qualifications = qualifications,
            submissions = submissions,
            parties = parties,
            contracts = contracts,
            bids = bids,
            parameters = parameters
        ).doOnError { return MaybeFail.fail(it) }

        val entities = parameters.entities
        val documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document> = data.associateBy { it.id }

        updateTender(tender, entities, documentsByIds, context).doOnFail { return MaybeFail.fail(it) }
        updateAwards(awards, entities, documentsByIds, context).doOnFail { return MaybeFail.fail(it) }
        updateQualifications(qualifications, entities, documentsByIds, context).doOnFail { return MaybeFail.fail(it) }

        submissions?.let { updateSubmissions(it, entities, documentsByIds, context) }
            ?.doOnFail { return MaybeFail.fail(it) }

        updateParties(parties, entities, documentsByIds, context)
            .doOnFail { return MaybeFail.fail(it) }

        val updatedContracts = contracts
            .updateContracts(entities, documentsByIds).orReturnFail { return MaybeFail.fail(it) }
            .updateContractsConfirmationResponse(entities, documentsByIds).orReturnFail { return MaybeFail.fail(it) }

        context.contracts = updatedContracts

        updateBids(bids, entities, documentsByIds, context)
            .doOnFail { return MaybeFail.fail(it) }

        return MaybeFail.none()
    }

    private fun validateData(
        tender: Tender?,
        awards: Awards,
        qualifications: Qualifications,
        submissions: Submissions?,
        parties: Parties,
        contracts: Contracts,
        bids: Bids?,
        parameters: Parameters,
        data: OpenAccessAction.Result
    ): ValidationResult<Fail.Incident> {

        val contextDocumentIds =
            getAllDocuments(tender, awards, qualifications, submissions, parties, contracts, bids, parameters)
                .orReturnFail { return ValidationResult.error(it) }

        data.map { it.id }
            .validate(duplicatesRule(DOCUMENT_ID_PATH))
            .validate(missingEntityRule(contextDocumentIds, DOCUMENT_ID_PATH))
            .validate(unknownEntityRule(contextDocumentIds, DOCUMENT_ID_PATH))
            .doOnError { return ValidationResult.error(it) }

        return ValidationResult.ok()
    }

    private fun Tender.updateAmendmentDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<List<Amendment>, Fail.Incident.Bpms> =
        this.amendments
            .map { amendment ->
                val updatedDocuments = amendment.documents
                    .map { document ->
                        documentsByIds[document.id]
                            ?.let {
                                document.copy(
                                    datePublished = it.datePublished,
                                    url = it.uri
                                )
                            }
                            ?: return failure(
                                Fail.Incident.Bpms.Context.UnConsistency.Update(
                                    name = "document",
                                    path = "tender.amendments[id:${amendment.id}].documents[id:${document.id}]",
                                    id = document.id.toString()
                                )
                            )
                    }
                amendment.copy(documents = Documents(updatedDocuments))
            }
            .asSuccess()

    private fun Tender.updateTenderDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<List<Document>, Fail.Incident.Bpms> =
        this.documents
            .map { document ->
                documentsByIds[document.id]
                    ?.let {
                        document.copy(
                            datePublished = it.datePublished,
                            url = it.uri
                        )
                    }
                    ?: return failure(
                        Fail.Incident.Bpms.Context.UnConsistency.Update(
                            name = "document",
                            path = "tender.documents[id:${document.id}]",
                            id = document.id.toString()
                        )
                    )
            }
            .asSuccess()

    private fun Awards.updateDocuments(
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Awards, Fail.Incident.Bpms> =
        if (EntityKey.AWARD in entities)
            this.updateDocuments(documentsByIds)
        else this.asSuccess()

    private fun Awards.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Awards, Fail.Incident.Bpms> {
        val updatedAwards = this.map { award ->
            val updatedDocuments = award.documents.map { document ->
                documentsByIds[document.id]
                    ?.let {
                        document.copy(datePublished = it.datePublished, url = it.uri)
                    }
                    ?: return failure(
                        Fail.Incident.Bpms.Context.UnConsistency.Update(
                            name = "document",
                            path = "awards[id:${award.id}]",
                            id = document.id.toString()
                        )
                    )
            }
            award.copy(documents = Documents(updatedDocuments))
        }
        return success(Awards(updatedAwards))
    }

    private fun Awards.updateRequirementRsDocuments(
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Awards, Fail.Incident.Bpms> =
        if (EntityKey.AWARD_REQUIREMENT_RESPONSE in entities)
            this.updateRequirementRsDocuments(documentsByIds)
        else
            this.asSuccess()

    private fun Awards.updateRequirementRsDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Awards, Fail.Incident.Bpms> {
        val updatedAwards = this.map { award ->
            val requirementResponse = award.requirementResponses.getOrNull(0)
            if (requirementResponse != null) {
                val updatedRequirementResponse = requirementResponse.let { requirementRs ->
                    val responder = requirementRs.responder
                    if (responder != null) {
                        val updatedResponder = responder.let { person ->
                            val updatedBusinessFunctions = person.businessFunctions
                                .map { businessFunction ->
                                    val updatedDocuments = businessFunction.documents
                                        .map { document ->
                                            documentsByIds[document.id]
                                                ?.let {
                                                    document.copy(datePublished = it.datePublished, url = it.uri)
                                                }
                                                ?: return failure(
                                                    Fail.Incident.Bpms.Context.UnConsistency.Update(
                                                        name = "document",
                                                        path = "awards[id:${award.id}].requirementResponse[id:${requirementRs.id}].responder.businessFunctions[id:${businessFunction.id}]",
                                                        id = document.id.toString()
                                                    )
                                                )
                                        }
                                    businessFunction.copy(documents = Documents(updatedDocuments))
                                }
                            person.copy(businessFunctions = BusinessFunctions(updatedBusinessFunctions))
                        }
                        requirementRs.copy(responder = updatedResponder)
                    } else
                        requirementRs
                }
                award.copy(requirementResponses = RequirementResponses(updatedRequirementResponse))
            } else award
        }
        return success(Awards(updatedAwards))
    }

    private fun Awards.updateSuppliersDocuments(
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Awards, Fail.Incident.Bpms> =
        if (EntityKey.AWARD_SUPPLIER in entities)
            this.updateSuppliersDocuments(documentsByIds)
        else
            this.asSuccess()

    private fun Awards.updateSuppliersDocuments(
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Awards, Fail.Incident.Bpms> {
        val updatedAwards = this.map { award ->
            val suppliers = award.suppliers
            val updatedSuppliers = suppliers.map { supplier ->
                val updatedPersons = supplier.persons.map { person ->
                    val updatedBusinessFunctions = person.businessFunctions
                        .map { businessFunction ->
                            val updatedDocuments = businessFunction.documents
                                .map { document ->
                                    documentsByIds[document.id]
                                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                                        ?: return failure(
                                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                                name = "document",
                                                path = "awards[id:${award.id}].suppliers[id:${supplier.id}].persons[id:${person.id}].businessFunctions[id:${businessFunction.id}]",
                                                id = document.id.toString()
                                            )
                                        )
                                }
                            businessFunction.copy(documents = Documents(updatedDocuments))
                        }
                    person.copy(businessFunctions = BusinessFunctions(updatedBusinessFunctions))
                }
                supplier.copy(persons = Persons(updatedPersons))
            }
            award.copy(suppliers = Organizations(updatedSuppliers))
        }
        return success(Awards(updatedAwards))
    }

    private fun Qualifications.updateReqRsDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Qualifications, Fail.Incident.Bpms> {
        val updatedQualifications = this.map { qualification ->
            val updatedRequirementResponses = qualification.requirementResponses
                .map { requirementResponse ->
                    val responder = requirementResponse.responder
                    val updatedResponder = if (responder != null) {
                        val updatedBusinessFunctions = responder.businessFunctions
                            .map { businessFunction ->
                                val updatedDocuments = businessFunction.documents
                                    .map { document ->
                                        documentsByIds[document.id]
                                            ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                                            ?: return failure(
                                                Fail.Incident.Bpms.Context.UnConsistency.Update(
                                                    name = "document",
                                                    path = "qualifications[id:${qualification.id}].requirementResponse[id:${requirementResponse.id}].responder.businessFunctions[id:${businessFunction.id}]",
                                                    id = document.id.toString()
                                                )
                                            )
                                    }
                                businessFunction.copy(documents = Documents(updatedDocuments))
                            }
                        responder.copy(businessFunctions = BusinessFunctions(updatedBusinessFunctions))
                    } else
                        responder
                    requirementResponse.copy(responder = updatedResponder)
                }
            qualification.copy(requirementResponses = RequirementResponses(updatedRequirementResponses))
        }
        return success(Qualifications(updatedQualifications))
    }

    private fun Qualifications.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Qualifications, Fail.Incident.Bpms> {
        val updatedQualifications = this.map { qualification ->
            val updatedDocuments = qualification.documents
                .map { document ->
                    documentsByIds[document.id]
                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                        ?: return failure(
                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                name = "document",
                                path = "qualifications[id:${qualification.id}]",
                                id = document.id.toString()
                            )
                        )
                }
            qualification.copy(documents = Documents(updatedDocuments))
        }

        return success(Qualifications(updatedQualifications))
    }

    private fun Submissions.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Submissions, Fail.Incident.Bpms> {
        val updatedSubmissions = this.details.map { submission ->
            val updatedDocuments = submission.documents
                .map { document ->
                    documentsByIds[document.id]
                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                        ?: return failure(
                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                name = "document",
                                path = "qualifications[id:${submission.id}]",
                                id = document.id.toString()
                            )
                        )
                }
            submission.copy(documents = Documents(updatedDocuments))
        }

        return success(Submissions(Details(updatedSubmissions)))
    }

    private fun Parties.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Parties, Fail.Incident.Bpms> {
        val updatedParties = this.map { party ->
            val updatedPersons = party.persons
                .map { person ->
                    val updatedBusinessFunctions = person.businessFunctions
                        .map { businessFunction ->
                            val updatedDocuments = businessFunction.documents
                                .map { document ->
                                    documentsByIds[document.id]
                                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                                        ?: return failure(
                                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                                name = "document",
                                                path = "parties[id:${party.id}].persons[id:${person.id}].businessFunctions[id:${businessFunction.id}]",
                                                id = document.id.toString()
                                            )
                                        )
                                }
                            businessFunction.copy(documents = Documents(updatedDocuments))
                        }
                    person.copy(businessFunctions = BusinessFunctions(updatedBusinessFunctions))
                }
            party.copy(persons = Persons(updatedPersons))
        }
        return success(Parties(updatedParties))
    }

    private fun getTenderDocumentsIds(
        tender: Tender?,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.TENDER]) {
            EntityValue.OPTIONAL -> getTenderDocumentsIdsOptional(tender)
            EntityValue.REQUIRED -> getTenderDocumentsIdsRequired(tender)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getTenderDocumentsIdsOptional(tender: Tender?): Result<List<DocumentId>, Fail.Incident> =
        tender?.documents
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()

    private fun getTenderDocumentsIdsRequired(tender: Tender?): Result<List<DocumentId>, Fail.Incident> {
        if (tender == null)
            return failure(DelegateIncident.PathMissing(path = "tender"))

        val documents = tender.documents
        if (documents.isEmpty())
            return failure(DelegateIncident.DocumentsMissing(path = "tender"))

        return documents.map { document -> document.id }
            .asSuccess()
    }

    private fun getTenderAmendmentDocumentsIds(
        tender: Tender?, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AMENDMENT]) {
            EntityValue.OPTIONAL -> getTenderAmendmentDocumentsIdsOptional(tender)
            EntityValue.REQUIRED -> getTenderAmendmentDocumentsIdsRequired(tender)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getTenderAmendmentDocumentsIdsOptional(tender: Tender?): Result<List<DocumentId>, Fail.Incident> =
        tender?.amendments
            ?.asSequence()
            ?.flatMap { amendment -> amendment.documents.asSequence() }
            ?.map { document -> document.id }
            ?.toList()
            .orEmpty()
            .asSuccess()

    private fun getTenderAmendmentDocumentsIdsRequired(tender: Tender?): Result<List<DocumentId>, Fail.Incident> {
        if (tender == null)
            return failure(DelegateIncident.PathMissing(path = "tender"))

        val amendments = tender.amendments
        if (amendments.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "tender.amendments"))

        return amendments
            .flatMap { amendment ->
                val documents = amendment.documents
                if (documents.isEmpty())
                    return failure(DelegateIncident.DocumentsMissing(path = "tender.amendments"))

                documents.map { document -> document.id }
            }
            .asSuccess()
    }

    private fun getAwardsDocumentsIds(
        awards: Awards, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD]) {
            EntityValue.OPTIONAL -> getAwardsDocumentsIdsOptional(awards)
            EntityValue.REQUIRED -> getAwardsDocumentsIdsRequired(awards)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAwardsDocumentsIdsOptional(awards: Awards): Result<List<DocumentId>, Fail.Incident> =
        awards.flatMap { award -> award.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getAwardsDocumentsIdsRequired(awards: Awards): Result<List<DocumentId>, Fail.Incident> {
        if (awards.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "awards"))

        return awards
            .flatMap { award ->
                val documents = award.documents
                if (documents.isEmpty())
                    return failure(DelegateIncident.DocumentsMissing(path = "awards.documents"))

                documents.map { document -> document.id }
            }
            .asSuccess()
    }

    private fun getAwardsSuppliersDocumentsIds(
        awards: Awards, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD_SUPPLIER]) {
            EntityValue.OPTIONAL -> getAwardsSuppliersDocumentIdsOptional(awards)
            EntityValue.REQUIRED -> getAwardsSuppliersDocumentsIdsRequired(awards)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAwardsSuppliersDocumentIdsOptional(awards: Awards): Result<List<DocumentId>, Fail.Incident> =
        awards.flatMap { award -> award.suppliers }
            .flatMap { supplier -> supplier.persons }
            .flatMap { person -> person.businessFunctions }
            .flatMap { businessFunction -> businessFunction.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getAwardsSuppliersDocumentsIdsRequired(awards: Awards): Result<List<DocumentId>, Fail.Incident> {
        if (awards.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "awards"))

        return awards
            .flatMap { award ->
                val suppliers = award.suppliers
                if (suppliers.isEmpty())
                    return failure(DelegateIncident.PathMissing(path = "awards.suppliers"))

                suppliers.flatMap { supplier ->
                    val persons = supplier.persons
                    if (persons.isEmpty())
                        return failure(DelegateIncident.PathMissing(path = "awards.suppliers.persons"))

                    persons.flatMap { person ->
                        val businessFunctions = person.businessFunctions
                        if (businessFunctions.isEmpty())
                            return failure(DelegateIncident.PathMissing(path = "awards.suppliers.persons.businessFunctions"))

                        businessFunctions.flatMap { businessFunction ->
                            val documents = businessFunction.documents
                            if (documents.isEmpty())
                                return failure(DelegateIncident.DocumentsMissing(path = "awards.suppliers.persons.businessFunctions"))

                            documents.map { document -> document.id }
                        }
                    }
                }
            }
            .asSuccess()
    }

    private fun getAwardRqRsDocumentsIds(
        awards: Awards, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD_REQUIREMENT_RESPONSE]) {
            EntityValue.OPTIONAL -> getAwardRqRsDocumentsIdsOptional(awards)
            EntityValue.REQUIRED -> getAwardRqRsDocumentsIdsRequired(awards)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAwardRqRsDocumentsIdsOptional(awards: Awards): Result<List<DocumentId>, Fail.Incident> =
        awards.asSequence()
            .map { award ->
                award.requirementResponses.getOrNull(0)
            }
            .flatMap { requirementResponse ->
                requirementResponse?.responder?.businessFunctions?.asSequence() ?: emptySequence()
            }
            .flatMap { businessFunction ->
                businessFunction.documents.asSequence()
            }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getAwardRqRsDocumentsIdsRequired(awards: Awards): Result<List<DocumentId>, Fail.Incident> {
        if (awards.isEmpty())
            failure(DelegateIncident.PathMissing(path = "awards"))

        return awards
            .flatMap { award ->
                val requirementResponses = award.requirementResponses
                if (requirementResponses.isEmpty())
                    return failure(DelegateIncident.PathMissing(path = "awards.requirementResponses"))

                requirementResponses.flatMap { response ->
                    val responder = response.responder
                        ?: return failure(DelegateIncident.PathMissing(path = "awards.requirementResponses.responder"))

                    val businessFunctions = responder.businessFunctions
                    if (businessFunctions.isEmpty())
                        return failure(DelegateIncident.PathMissing(path = "awards.requirementResponses.responder.businessFunctions"))

                    businessFunctions.flatMap { businessFunction ->
                        val documents = businessFunction.documents
                        if (documents.isEmpty())
                            return failure(DelegateIncident.DocumentsMissing(path = "awards.requirementResponses.responder.businessFunctions"))

                        documents.map { document -> document.id }
                    }
                }
            }
            .asSuccess()
    }

    private fun getSubmissionsDocumentsIds(
        submissions: Submissions?,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.SUBMISSION]) {
            EntityValue.OPTIONAL -> getSubmissionsDocumentsIdsOptional(submissions)
            EntityValue.REQUIRED -> getSubmissionsDocumentsIdsRequired(submissions)
            null -> success(emptyList())
        }

    private fun getSubmissionsDocumentsIdsOptional(submissions: Submissions?): Result<List<DocumentId>, Fail.Incident> =
        submissions
            ?.details
            ?.flatMap { submission ->
                submission.documents
                    .map { document -> document.id }
            }
            .orEmpty()
            .asSuccess()

    private fun getSubmissionsDocumentsIdsRequired(submissions: Submissions?): Result<List<DocumentId>, Fail.Incident> {
        if (submissions == null)
            return failure(DelegateIncident.PathMissing(path = "submissions"))

        val details = submissions.details
        if (details.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "submissions.details"))

        return details
            .flatMap { submission ->
                val documents = submission.documents
                if (documents.isEmpty())
                    return failure(DelegateIncident.DocumentsMissing(path = "submissions.details"))

                documents.map { document -> document.id }
            }
            .asSuccess()
    }

    private fun getQualificationDocumentsIds(
        qualifications: Qualifications,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.QUALIFICATION]) {
            EntityValue.OPTIONAL -> getQualificationDocumentsIdsOptional(qualifications)
            EntityValue.REQUIRED -> getQualificationDocumentsIdsRequired(qualifications)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getQualificationDocumentsIdsOptional(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> =
        qualifications
            .flatMap { qualification ->
                qualification.documents
                    .map { document -> document.id }
            }
            .asSuccess()

    private fun getQualificationDocumentsIdsRequired(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> {
        if (qualifications.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "qualifications"))

        return qualifications
            .flatMap { qualification ->
                val documents = qualification.documents
                if (documents.isEmpty())
                    return failure(DelegateIncident.DocumentsMissing(path = "qualifications"))

                documents.map { document -> document.id }
            }
            .asSuccess()
    }

    private fun getQualificationRqRsDocumentsIds(
        qualifications: Qualifications,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.QUALIFICATION_REQUIREMENT_RESPONSE]) {
            EntityValue.OPTIONAL -> getQualificationRqRsDocumentsIdsOptional(qualifications)
            EntityValue.REQUIRED -> getQualificationRqRsDocumentsIdsRequired(qualifications)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getQualificationRqRsDocumentsIdsOptional(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> =
        qualifications
            .asSequence()
            .flatMap { qualification -> qualification.requirementResponses.asSequence() }
            .flatMap { requirementResponse ->
                requirementResponse.responder?.businessFunctions?.asSequence() ?: emptySequence()
            }
            .flatMap { businessFunction -> businessFunction.documents.asSequence() }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getQualificationRqRsDocumentsIdsRequired(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> {

        if (qualifications.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "qualifications"))

        return qualifications
            .flatMap { qualification ->
                val requirementResponses = qualification.requirementResponses
                if (requirementResponses.isEmpty())
                    return failure(DelegateIncident.PathMissing(path = "qualifications.requirementResponses"))

                requirementResponses.flatMap { response ->
                    val responder = response.responder
                        ?: return failure(DelegateIncident.PathMissing(path = "qualifications.requirementResponses.responder"))

                    val businessFunctions = responder.businessFunctions
                    if (businessFunctions.isEmpty())
                        return failure(DelegateIncident.PathMissing(path = "qualifications.requirementResponses.responder.businessFunctions"))

                    businessFunctions.flatMap { businessFunction ->
                        val documents = businessFunction.documents
                        if (documents.isEmpty())
                            return failure(DelegateIncident.DocumentsMissing(path = "qualifications.requirementResponses.responder.businessFunctions"))

                        documents.map { document -> document.id }
                    }
                }
            }
            .asSuccess()
    }

    private fun getBidsDocumentsIds(
        bids: Bids?, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.BID]) {
            EntityValue.OPTIONAL -> getBidsDocumentsIdsOptional(bids)
            EntityValue.REQUIRED -> getBidsDocumentsIdsRequired(bids)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getBidsDocumentsIdsOptional(bids: Bids?): Result<List<DocumentId>, Fail.Incident> =
        bids?.details.orEmpty()
            .flatMap { contract -> contract.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getBidsDocumentsIdsRequired(bids: Bids?): Result<List<DocumentId>, Fail.Incident> {
        if (bids == null)
            return failure(DelegateIncident.PathMissing(path = "bids"))

        val details = bids.details
        if (details.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "bids.details"))

        return details
            .flatMap { bid ->
                val documents = bid.documents
                if (documents.isEmpty())
                    return failure(DelegateIncident.DocumentsMissing(path = "bids.details"))

                documents.map { document -> document.id }
            }
            .asSuccess()
    }

    private fun getPartiesDocumentsIds(
        parties: Parties,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.PARTIES]) {
            EntityValue.OPTIONAL -> getPartiesDocumentsIdsOptional(parties)
            EntityValue.REQUIRED -> getPartiesDocumentsIdsRequired(parties)
            null -> success(emptyList())
        }

    private fun getPartiesDocumentsIdsOptional(parties: Parties): Result<List<DocumentId>, Fail.Incident> =
        parties.asSequence()
            .flatMap { party -> party.persons.asSequence() }
            .flatMap { person -> person.businessFunctions.asSequence() }
            .flatMap { businessFunction -> businessFunction.documents.asSequence() }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getPartiesDocumentsIdsRequired(parties: Parties): Result<List<DocumentId>, Fail.Incident> {
        if (parties.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "parties"))

        return parties
            .flatMap { party ->
                val persons = party.persons
                if (persons.isEmpty())
                    return failure(DelegateIncident.PathMissing(path = "parties.persons"))

                persons.flatMap { person ->
                    val businessFunctions = person.businessFunctions
                    if (businessFunctions.isEmpty())
                        return failure(DelegateIncident.PathMissing(path = "parties.persons.businessFunctions"))

                    businessFunctions.flatMap { businessFunction ->
                        val documents = businessFunction.documents
                        if (documents.isEmpty())
                            return failure(DelegateIncident.DocumentsMissing(path = "parties.persons.businessFunctions"))

                        documents.map { document -> document.id }
                    }
                }
            }
            .asSuccess()
    }

    private fun getContractsDocumentsIds(
        contracts: Contracts, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.CONTRACT]) {
            EntityValue.OPTIONAL -> getContractsDocumentIdsOptional(contracts)
            EntityValue.REQUIRED -> getContractsDocumentsIdsRequired(contracts)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getContractsDocumentIdsOptional(contracts: Contracts): Result<List<DocumentId>, Fail.Incident> =
        contracts
            .flatMap { contract -> contract.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getContractsDocumentsIdsRequired(contracts: Contracts): Result<List<DocumentId>, Fail.Incident> {
        if (contracts.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "contracts"))

        return contracts
            .flatMap { award ->
                val documents = award.documents
                if (documents.isEmpty())
                    return failure(DelegateIncident.DocumentsMissing(path = "contracts.documents"))

                documents.map { document -> document.id }
            }
            .asSuccess()
    }

    private fun getContractsConfirmationResponseDocumentsIds(
        contracts: Contracts, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.CONTRACT_CONFIRMATION_RESPONSE]) {
            EntityValue.OPTIONAL -> getContractsConfirmationResponseDocumentsIdsOptional(contracts)
            EntityValue.REQUIRED -> getContractsConfirmationResponseDocumentsIdsRequired(contracts)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getContractsConfirmationResponseDocumentsIdsOptional(contracts: Contracts): Result<List<DocumentId>, Fail.Incident> =
        contracts
            .flatMap { contract -> contract.confirmationResponses }
            .flatMap { it.relatedPerson?.businessFunctions.orEmpty() }
            .flatMap { it.documents }
            .map { it.id }
            .asSuccess()

    private fun getContractsConfirmationResponseDocumentsIdsRequired(contracts: Contracts): Result<List<DocumentId>, Fail.Incident> {
        if (contracts.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "contracts"))

        val confirmationResponses = contracts.flatMap { contract -> contract.confirmationResponses }
        if (confirmationResponses.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "contracts.confirmationResponses"))

        val businessFunctions = confirmationResponses
            .flatMap { confirmationResponse ->
                confirmationResponse.relatedPerson?.businessFunctions
                    ?: return failure(DelegateIncident.PathMissing(path = "contracts.confirmationResponses.relatedPerson"))
            }
        if (businessFunctions.isEmpty())
            return failure(DelegateIncident.PathMissing(path = "contracts.confirmationResponses.relatedPerson.businessFunctions"))

        val documents = businessFunctions.flatMap { it.documents }
        if (documents.isEmpty())
            return failure(DelegateIncident.DocumentsMissing(path = "contracts.confirmationResponses.relatedPerson.businessFunctions.documents"))

        return documents.map { document -> document.id }
            .asSuccess()
    }

    private fun getAllDocuments(
        tender: Tender?,
        awards: Awards,
        qualifications: Qualifications,
        submissions: Submissions?,
        parties: Parties,
        contracts: Contracts,
        bids: Bids?,
        parameters: Parameters
    ): Result<List<DocumentId>, Fail.Incident> {
        val entities = parameters.entities

        val amendmentDocuments: List<DocumentId> = getTenderAmendmentDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val tenderDocuments: List<DocumentId> = getTenderDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val awardRequirementResponseDocuments = getAwardRqRsDocumentsIds(awards, entities)
            .orForwardFail { fail -> return fail }

        val qualificationDocuments: List<DocumentId> = getQualificationDocumentsIds(qualifications, entities)
            .orForwardFail { fail -> return fail }

        val qualificationReqRsDocuments: List<DocumentId> = getQualificationRqRsDocumentsIds(qualifications, entities)
            .orForwardFail { fail -> return fail }

        val submissionsDocuments: List<DocumentId> = getSubmissionsDocumentsIds(submissions, entities)
            .orForwardFail { fail -> return fail }

        val partiesDocuments: List<DocumentId> = getPartiesDocumentsIds(parties, entities)
            .orForwardFail { fail -> return fail }

        val awardDocuments: List<DocumentId> = getAwardsDocumentsIds(awards, entities)
            .orForwardFail { fail -> return fail }

        val awardSuppliersDocuments: List<DocumentId> = getAwardsSuppliersDocumentsIds(awards, entities)
            .orForwardFail { fail -> return fail }

        val contractsDocuments: List<DocumentId> = getContractsDocumentsIds(contracts, entities)
            .orForwardFail { fail -> return fail }

        val bidsDocuments: List<DocumentId> = getBidsDocumentsIds(bids, entities)
            .orForwardFail { fail -> return fail }

        val contractsConfirmationResponseDocuments: List<DocumentId> = getContractsConfirmationResponseDocumentsIds(contracts, entities)
            .orForwardFail { fail -> return fail }

        return amendmentDocuments
            .asSequence()
            .plus(tenderDocuments)
            .plus(awardRequirementResponseDocuments)
            .plus(qualificationDocuments)
            .plus(qualificationReqRsDocuments)
            .plus(submissionsDocuments)
            .plus(partiesDocuments)
            .plus(awardDocuments)
            .plus(awardSuppliersDocuments)
            .plus(contractsDocuments)
            .plus(bidsDocuments)
            .plus(contractsConfirmationResponseDocuments)
            .toList()
            .asSuccess()
    }

    private fun updateTender(
        tender: Tender?,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val updatedAmendments = if (EntityKey.AMENDMENT in entities) {
            tender?.updateAmendmentDocuments(documentsByIds)
                ?.orReturnFail { return MaybeFail.fail(it) }
                .orEmpty()
        } else
            tender?.amendments.orEmpty()

        val updatedTenderDocuments = if (EntityKey.TENDER in entities) {
            tender?.updateTenderDocuments(documentsByIds)
                ?.orReturnFail { return MaybeFail.fail(it) }
                .orEmpty()
        } else
            tender?.documents.orEmpty()

        if (tender != null)
            context.tender = tender.copy(
                documents = Documents(updatedTenderDocuments),
                amendments = Amendments(updatedAmendments)
            )

        return MaybeFail.none()
    }

    private fun updateAwards(
        awards: Awards,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val updatedAwards = awards.updateDocuments(entities, documentsByIds)
            .bind { it.updateRequirementRsDocuments(entities, documentsByIds) }
            .bind { it.updateSuppliersDocuments(entities, documentsByIds) }
            .orForwardFail { return it.asMaybeFail }

        context.awards = updatedAwards

        return MaybeFail.none()
    }

    private fun updateQualifications(
        qualifications: Qualifications,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val qualificationsWithUpdatedReqRsDocuments: Qualifications =
            if (EntityKey.QUALIFICATION_REQUIREMENT_RESPONSE in entities)
                qualifications.updateReqRsDocuments(documentsByIds)
                    .orReturnFail { return MaybeFail.fail(it) }
            else
                qualifications

        val qualificationsWithAllDocumentsUpdated: Qualifications = if (EntityKey.QUALIFICATION in entities)
            qualificationsWithUpdatedReqRsDocuments.updateDocuments(documentsByIds)
                .orReturnFail { return MaybeFail.fail(it) }
        else
            qualificationsWithUpdatedReqRsDocuments

        context.qualifications = qualificationsWithAllDocumentsUpdated

        return MaybeFail.none()
    }

    private fun updateSubmissions(
        submissions: Submissions,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val submissionsWithUpdatedDocuments: Submissions = if (EntityKey.SUBMISSION in entities)
            submissions.updateDocuments(documentsByIds)
                .orReturnFail { return MaybeFail.fail(it) }
        else
            submissions

        context.submissions = submissionsWithUpdatedDocuments

        return MaybeFail.none()
    }

    private fun updateParties(
        parties: Parties,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val partiesWithUpdatedDocuments: Parties = if (EntityKey.PARTIES in entities)
            parties.updateDocuments(documentsByIds)
                .orReturnFail { return MaybeFail.fail(it) }
        else
            parties

        context.parties = partiesWithUpdatedDocuments

        return MaybeFail.none()
    }

    private fun Contracts.updateContracts(
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Contracts, Fail.Incident> =
        if (EntityKey.CONTRACT in entities)
            this
                .updateDocuments(documentsByIds)
                .orForwardFail { return it }
                .asSuccess()
        else
            this.asSuccess()

    private fun Contracts.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Contracts, Fail.Incident.Bpms> {
        val updatedContracts = this.map { contract ->
            val updatedDocuments = contract.documents
                .map { document ->
                    documentsByIds[document.id]
                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                        ?: return failure(
                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                name = "document",
                                path = "contracts[id:${contract.id}]",
                                id = document.id.toString()
                            )
                        )
                }
            contract.copy(documents = Documents(updatedDocuments))
        }
        return success(Contracts(updatedContracts))
    }

    private fun Contracts.updateContractsConfirmationResponse(
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>
    ): Result<Contracts, Fail.Incident> =
        if (EntityKey.CONTRACT_CONFIRMATION_RESPONSE in entities)
            this
                .map { contract ->
                    val updatedConfirmationResponses = contract.confirmationResponses
                        .map { confirmationResponse ->
                            val updatedRelatedPerson = confirmationResponse.relatedPerson
                                ?.let { person ->
                                    val updatedBusinessFunctions = person.businessFunctions
                                        .updateDocuments(documentsByIds)
                                        .orForwardFail { return it }
                                    person.copy(businessFunctions = updatedBusinessFunctions)
                                }
                            confirmationResponse.copy(relatedPerson = updatedRelatedPerson)
                        }
                    contract.copy(confirmationResponses = ConfirmationResponses(updatedConfirmationResponses))
                }
                .let { Contracts(it) }
                .asSuccess()
        else
            this.asSuccess()


    private fun BusinessFunctions.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<BusinessFunctions, Fail.Incident.Bpms> {
        val updatedBusinesFunctions = this.map { businessFunction ->
            val updatedDocuments = businessFunction.documents
                .map { document ->
                    documentsByIds[document.id]
                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                        ?: return failure(
                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                name = "document",
                                path = "contracts[id:${businessFunction.id}]",
                                id = document.id.toString()
                            )
                        )
                }
            businessFunction.copy(documents = Documents(updatedDocuments))
        }
        return success(BusinessFunctions(updatedBusinesFunctions))
    }

    private fun updateBids(
        bids: Bids?,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val bidsWithUpdatedDocuments: Bids? = if (EntityKey.BID in entities)
            bids
                ?.updateDocuments(documentsByIds)
                ?.orReturnFail { return MaybeFail.fail(it) }
        else
            bids

        context.bids = bidsWithUpdatedDocuments

        return MaybeFail.none()
    }

    private fun Bids.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Bids, Fail.Incident.Bpms> {
        val updatedBids = this.details.map { bid ->
            val updatedDocuments = bid.documents
                .map { document ->
                    documentsByIds[document.id]
                        ?.let { document.copy(datePublished = it.datePublished, url = it.uri) }
                        ?: return failure(
                            Fail.Incident.Bpms.Context.UnConsistency.Update(
                                name = "document",
                                path = "bids[id:${bid.id}]",
                                id = document.id.toString()
                            )
                        )
                }
            bid.copy(documents = Documents(updatedDocuments))
        }

        return this
            .copy(details = BidsDetails(updatedBids))
            .asSuccess()
    }

    class Parameters(
        val entities: Map<EntityKey, EntityValue>
    )

    enum class EntityKey(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        AWARD("award"),
        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        AWARD_SUPPLIER("award.supplier"),
        BID("bid"),
        CONTRACT("contract"),
        CONTRACT_CONFIRMATION_RESPONSE("contract.confirmationResponse"),
        PARTIES("parties"),
        QUALIFICATION("qualification"),
        QUALIFICATION_REQUIREMENT_RESPONSE("qualification.requirementResponse"),
        SUBMISSION("submission"),
        TENDER("tender"),
        ;

        override fun toString(): String = key

        companion object : EnumElementProvider<EntityKey>(info = info())
    }

    enum class EntityValue(override val key: String) : EnumElementProvider.Key {

        OPTIONAL("optional"),
        REQUIRED("required"),
        ;

        override fun toString(): String = key

        companion object : EnumElementProvider<EntityValue>(info = info())
    }

    sealed class DelegateIncident(description: String) : Fail.Incident.Bpms.DelegateRule(description) {
        override val exception: Exception? = null

        class DocumentsMissing(path: String) :
            DelegateIncident("Error: 'FR.DEL-0.5.10.2' - Documents is missing, path: '$path'.")

        class PathMissing(path: String) :
            DelegateIncident("Error: 'FR.DEL-0.5.10.4' - A part of the path is missing, path: '$path'.")
    }
}
