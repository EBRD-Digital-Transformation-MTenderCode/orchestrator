package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getElementIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.ValidationResult
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.functional.bind
import com.procurement.orchestrator.domain.functional.validate
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.util.extension.asList
import com.procurement.orchestrator.domain.util.extension.toSetBy
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.rule.duplicatesRule
import com.procurement.orchestrator.infrastructure.bpms.delegate.rule.missingEntityRule
import com.procurement.orchestrator.infrastructure.bpms.delegate.rule.unknownEntityRule
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.storage.action.OpenAccessAction
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
        private const val TENDER_PATH = "tender"
        private const val AWARDS_PATH = "awards"
        private const val AMENDMENTS_PATH = "tender.amendments"
        private const val BUSINESS_FUNCTIONS_PATH = "awards.requirementResponses.responder"
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

        val documentIds = getAllDocuments(context.tender, context.awards, parameters)
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
        data: OpenAccessAction.Result
    ): MaybeFail<Fail.Incident> {

        if (data.isEmpty())
            return MaybeFail.none()

        val tender = context.tender
        val awards = context.awards
        validateData(data = data, tender = tender, awards = awards, parameters = parameters)
            .doOnError { return MaybeFail.fail(it) }

        val entities = parameters.entities
        val documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document> = data.associateBy { it.id }

        val updatedAmendments = if (EntityKey.AMENDMENT in entities) {
            if (tender == null)
                return MaybeFail.fail(Fail.Incident.Bpms.Context.Missing(name = TENDER_PATH))
            tender.updateAmendmentDocuments(documentsByIds)
                .orReturnFail { return MaybeFail.fail(it) }
        } else
            tender?.amendments.orEmpty()

        val updatedTenderDocuments = if (EntityKey.TENDER in entities) {
            if (tender == null)
                return MaybeFail.fail(Fail.Incident.Bpms.Context.Missing(name = TENDER_PATH))
            tender
                .updateTenderDocuments(documentsByIds)
                .orReturnFail { return MaybeFail.fail(it) }
        } else
            tender?.documents.orEmpty()

        if (tender != null)
            context.tender = tender.copy(
                documents = updatedTenderDocuments,
                amendments = updatedAmendments
            )

        val updatedAwards: Awards = success(awards)
            .bind {
                if (EntityKey.AWARD_REQUIREMENT_RESPONSE in entities)
                    it.updateDocuments(documentsByIds)
                else
                    success(it)
            }
            .orReturnFail { return MaybeFail.fail(it) }

        context.awards = updatedAwards

        return MaybeFail.none()
    }

    private fun validateData(
        tender: Tender?, awards: Awards, parameters: Parameters, data: OpenAccessAction.Result
    ): ValidationResult<Fail.Incident> {

        val contextDocumentIds = getAllDocuments(tender, awards, parameters)
            .orReturnFail { return ValidationResult.error(it) }
            .toSet()

        data.validate(duplicatesRule(DOCUMENT_ID_PATH))
            .orReturnFail { return ValidationResult.error(it) }
            .toSetBy { it.id }
            .validate(missingEntityRule(contextDocumentIds, DOCUMENT_ID_PATH))
            .validate(unknownEntityRule(contextDocumentIds, DOCUMENT_ID_PATH))
            .doOnError { return ValidationResult.error(it) }

        return ValidationResult.ok()
    }

    private fun Tender.updateAmendmentDocuments(documentsByIds: Map<DocumentId, OpenAccessAction.Result.Document>): Result<List<Amendment>, Fail.Incident.Bpms> =
        this.amendments
            .getElementIfOnlyOne(AMENDMENTS_PATH)
            .orForwardFail { fail -> return fail }
            .let { amendment ->
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
                amendment.copy(documents = updatedDocuments)
            }
            .asList()
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
        val updatedAwards = this
            .getElementIfOnlyOne(AWARDS_PATH)
            .orForwardFail { fail -> return fail }
            .let { award ->
                val updatedRequirementResponses = award.getRequirementResponseIfOnlyOne()
                    .orForwardFail { fail -> return fail }
                    .let { requirementResponse ->
                        val updatedResponder = requirementResponse.getResponder()
                            .orForwardFail { fail -> return fail }
                            .let { person ->
                                val updatedBusinessFunctions = person.getBusinessFunctionsIfNotEmpty(path = BUSINESS_FUNCTIONS_PATH)
                                    .orForwardFail { fail -> return fail }
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
                                                            path = "awards[id:$award.id].requirementResponse[id:${requirementResponse.id}].responder.businessFunctions[id:${businessFunction.id}]",
                                                            id = document.id.toString()
                                                        )
                                                    )
                                            }
                                        businessFunction.copy(documents = updatedDocuments)
                                    }
                                person.copy(businessFunctions = updatedBusinessFunctions)
                            }
                        requirementResponse.copy(responder = updatedResponder)
                    }
                award.copy(requirementResponses = listOf(updatedRequirementResponses))
            }
        return success(Awards(updatedAwards))
    }

    private fun getTenderDocumentsIds(
        tender: Tender?,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> {
        val tenderKey = EntityKey.TENDER
        return if (tenderKey in entities.keys) {
            when (entities.getValue(tenderKey)) {
                EntityValue.OPTIONAL -> getTenderDocumentsIdsOptional(tender)
                EntityValue.REQUIRED -> getTenderDocumentsIdsRequired(tender)
                    .doOnError { error -> return error.asFailure() }
            }
        } else
            emptyList<DocumentId>().asSuccess()
    }

    private fun getTenderDocumentsIdsOptional(tender: Tender?): Result<List<DocumentId>, Fail.Incident> =
        tender?.documents
            ?.asSequence()
            ?.map { document -> document.id }
            ?.toList()
            .orEmpty()
            .asSuccess()

    private fun getTenderDocumentsIdsRequired(tender: Tender?): Result<List<DocumentId>, Fail.Incident> {
        if (tender == null)
            return failure(Fail.Incident.Bpms.Context.Missing(name = TENDER_PATH))

        return tender.getDocumentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { document -> document.id }
            .asSuccess()
    }

    private fun getAmendmentDocumentsIds(
        tender: Tender?, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> {
        val amendmentKey = EntityKey.AMENDMENT
        return if (amendmentKey in entities) {
            when (entities.getValue(amendmentKey)) {
                EntityValue.OPTIONAL -> getAmendmentDocumentsIdsOptional(tender)
                EntityValue.REQUIRED -> getAmendmentDocumentsIdsRequired(tender)
                    .doOnError { error -> return error.asFailure() }
            }
        } else
            emptyList<DocumentId>().asSuccess()
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
            return failure(Fail.Incident.Bpms.Context.Missing(name = TENDER_PATH))

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
    ): Result<List<DocumentId>, Fail.Incident> {
        val awardKey = EntityKey.AWARD_REQUIREMENT_RESPONSE
        return if (awardKey in entities) {
            when (entities.getValue(awardKey)) {
                EntityValue.OPTIONAL -> getAwardDocumentsIdsOptional(awards)
                EntityValue.REQUIRED -> getAwardDocumentsIdsRequired(awards)
                    .doOnError { error -> return error.asFailure() }
            }
        } else
            emptyList<DocumentId>().asSuccess()
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
        awards.getIfNotEmpty(name = AWARDS_PATH)
            .orForwardFail { fail -> return fail }
            .flatMap { award ->
                award.getRequirementResponseIfOnlyOne()
                    .orForwardFail { fail -> return fail }
                    .getResponder()
                    .orForwardFail { fail -> return fail }
                    .getBusinessFunctionsIfNotEmpty(path = BUSINESS_FUNCTIONS_PATH)
                    .orForwardFail { fail -> return fail }
                    .flatMap { businessFunction ->
                        businessFunction.getDocumentsIfNotEmpty()
                            .orForwardFail { fail -> return fail }
                    }
            }
            .map { document -> document.id }
            .asSuccess()

    private fun getAllDocuments(
        tender: Tender?, awards: Awards, parameters: Parameters
    ): Result<List<DocumentId>, Fail.Incident> {
        val entities = parameters.entities

        val documentOfAmendmentsOfTender: List<DocumentId> = getAmendmentDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val documentOfTender: List<DocumentId> = getTenderDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val documentOfAwards: List<DocumentId> = getAwardRequirementResponseDocumentsIds(awards, entities)
            .orForwardFail { fail -> return fail }

        return documentOfAmendmentsOfTender
            .plus(documentOfTender)
            .plus(documentOfAwards)
            .asSuccess()
    }

    class Parameters(
        val entities: Map<EntityKey, EntityValue>
    )

    enum class EntityKey(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        TENDER("tender");

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
