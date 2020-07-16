package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getElementIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getQualificationBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getQualificationDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
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
import com.procurement.orchestrator.domain.functional.validate
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
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
        private const val TENDER_ATTRIBUTE_NAME = "tender"
        private const val AWARDS_ATTRIBUTE_NAME = "awards"
        private const val QUALIFICATIONS_ATTRIBUTE_NAME = "qualifications"
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

        val documentIds = getAllDocuments(context.tender, context.awards, context.qualifications, parameters)
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

        validateData(
            data = data,
            tender = tender,
            awards = awards,
            qualifications = qualifications,
            parameters = parameters
        ).doOnError { return MaybeFail.fail(it) }

        val entities = parameters.entities
        val documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document> = data.associateBy { it.id }

        updateTender(tender, entities, documentsByIds, context).doOnFail { return MaybeFail.fail(it) }
        updateAwards(awards, entities, documentsByIds, context).doOnFail { return MaybeFail.fail(it) }
        updateQualifications(qualifications, entities, documentsByIds, context).doOnFail { return MaybeFail.fail(it) }

        return MaybeFail.none()
    }

    private fun validateData(
        tender: Tender?,
        awards: Awards,
        qualifications: Qualifications,
        parameters: Parameters,
        data: OpenAccessAction.Result
    ): ValidationResult<Fail.Incident> {

        val contextDocumentIds = getAllDocuments(tender, awards, qualifications, parameters)
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

    private fun Awards.updateDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<Awards, Fail.Incident.Bpms> {
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
            return failure(Fail.Incident.Bpms.Context.Missing(name = TENDER_ATTRIBUTE_NAME))

        return tender.getDocumentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { document -> document.id }
            .asSuccess()
    }

    private fun getAmendmentDocumentsIds(
        tender: Tender?, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AMENDMENT]) {
            EntityValue.OPTIONAL -> getAmendmentDocumentsIdsOptional(tender)
            EntityValue.REQUIRED -> getAmendmentDocumentsIdsRequired(tender)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAmendmentDocumentsIdsOptional(tender: Tender?): Result<List<DocumentId>, Fail.Incident> =
        tender?.amendments
            ?.asSequence()
            ?.flatMap { amendment -> amendment.documents.asSequence() }
            ?.map { document -> document.id }
            ?.toList()
            .orEmpty()
            .asSuccess()

    private fun getAmendmentDocumentsIdsRequired(tender: Tender?): Result<List<DocumentId>, Fail.Incident> {
        if (tender == null)
            return failure(Fail.Incident.Bpms.Context.Missing(name = TENDER_ATTRIBUTE_NAME))

        return tender.getAmendmentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { amendment ->
                amendment.getDocumentsIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .map { document -> document.id }
            .asSuccess()
    }

    private fun getAwardRequirementResponseDocumentsIds(
        awards: Awards, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD_REQUIREMENT_RESPONSE]) {
            EntityValue.OPTIONAL -> getAwardDocumentsIdsOptional(awards)
            EntityValue.REQUIRED -> getAwardDocumentsIdsRequired(awards)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAwardDocumentsIdsOptional(awards: Awards): Result<List<DocumentId>, Fail.Incident> =
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

    private fun getAwardDocumentsIdsRequired(awards: Awards): Result<List<DocumentId>, Fail.Incident> =
        awards.getIfNotEmpty(name = AWARDS_ATTRIBUTE_NAME)
            .orForwardFail { fail -> return fail }
            .flatMap { award ->
                award.getRequirementResponseIfOnlyOne()
                    .orForwardFail { fail -> return fail }
                    .getResponder()
                    .orForwardFail { fail -> return fail }
                    .getAwardBusinessFunctionsIfNotEmpty()
                    .orForwardFail { fail -> return fail }
                    .flatMap { businessFunction ->
                        businessFunction.getAwardDocumentsIfNotEmpty()
                            .orForwardFail { fail -> return fail }
                    }
            }
            .map { document -> document.id }
            .asSuccess()

    private fun getQualificationReqRsDocumentsIds(
        qualifications: Qualifications,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.QUALIFICATION_REQUIREMENT_RESPONSE]) {
            EntityValue.OPTIONAL -> getQualificationReqRsDocumentsIdsOptional(qualifications)
            EntityValue.REQUIRED -> getQualificationReqRsDocumentsIdsRequired(qualifications)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getQualificationReqRsDocumentsIdsOptional(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> =
        qualifications
            .asSequence()
            .flatMap { qualification -> qualification.requirementResponses.asSequence() }
            .flatMap { requirementResponse -> requirementResponse.responder?.businessFunctions?.asSequence() ?: emptySequence() }
            .flatMap { businessFunction -> businessFunction.documents.asSequence() }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getQualificationReqRsDocumentsIdsRequired(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> =
        qualifications
            .getElementIfOnlyOne(name = QUALIFICATIONS_ATTRIBUTE_NAME)
            .orForwardFail { fail -> return fail }
            .getRequirementResponseIfOnlyOne()
            .orForwardFail { fail -> return fail }
            .getResponder()
            .orForwardFail { fail -> return fail }
            .getQualificationBusinessFunctionsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { businessFunction ->
                businessFunction.getQualificationDocumentsIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .map { document -> document.id }
            .toList()
            .asSuccess()

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
            .getOrNull(0)
            ?.documents
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()

    private fun getQualificationDocumentsIdsRequired(qualifications: Qualifications): Result<List<DocumentId>, Fail.Incident> =
        qualifications
            .getElementIfOnlyOne(name = QUALIFICATIONS_ATTRIBUTE_NAME)
            .orForwardFail { fail -> return fail }
            .getDocumentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getAllDocuments(
        tender: Tender?, awards: Awards, qualifications: Qualifications, parameters: Parameters
    ): Result<List<DocumentId>, Fail.Incident> {
        val entities = parameters.entities

        val amendmentDocuments: List<DocumentId> = getAmendmentDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val tenderDocuments: List<DocumentId> = getTenderDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val awardRequirementResponseDocuments = getAwardRequirementResponseDocumentsIds(awards, entities)
            .orForwardFail { fail -> return fail }

        val qualificationDocuments: List<DocumentId> = getQualificationDocumentsIds(qualifications, entities)
            .orForwardFail { fail -> return fail }

        val qualificationReqRsDocuments: List<DocumentId> = getQualificationReqRsDocumentsIds(qualifications, entities)
            .orForwardFail { fail -> return fail }

        return amendmentDocuments
            .plus(tenderDocuments)
            .plus(awardRequirementResponseDocuments)
            .plus(qualificationDocuments)
            .plus(qualificationReqRsDocuments)
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
        val updatedAwards: Awards = if (EntityKey.AWARD_REQUIREMENT_RESPONSE in entities)
            awards.updateDocuments(documentsByIds)
                .orReturnFail { return MaybeFail.fail(it) }
        else
            awards

        context.awards = updatedAwards

        return MaybeFail.none()
    }

    private fun updateQualifications(
        qualifications: Qualifications,
        entities: Map<EntityKey, EntityValue>,
        documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>,
        context: CamundaGlobalContext
    ): MaybeFail<Fail.Incident> {
        val qualificationsWithUpdatedReqRsDocuments: Qualifications = if (EntityKey.QUALIFICATION_REQUIREMENT_RESPONSE in entities)
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

    class Parameters(
        val entities: Map<EntityKey, EntityValue>
    )

    enum class EntityKey(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        TENDER("tender"),
        QUALIFICATION_REQUIREMENT_RESPONSE("qualification.requirementResponse"),
        QUALIFICATION("qualification");

        override fun toString(): String = key

        companion object : EnumElementProvider<EntityKey>(info = info())
    }

    enum class EntityValue(override val key: String) : EnumElementProvider.Key {

        REQUIRED("required"),
        OPTIONAL("optional");

        override fun toString(): String = key

        companion object : EnumElementProvider<EntityValue>(info = info())
    }
}
