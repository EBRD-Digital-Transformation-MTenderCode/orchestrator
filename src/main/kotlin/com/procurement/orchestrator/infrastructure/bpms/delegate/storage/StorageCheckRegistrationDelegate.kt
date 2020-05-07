package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getCandidatesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getPersonesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.storage.action.CheckRegistrationAction
import org.springframework.stereotype.Component

@Component
class StorageCheckRegistrationDelegate(
    logger: Logger,
    private val client: StorageClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<StorageCheckRegistrationDelegate.Parameters, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        private const val TENDER_ATTRIBUTE_NAME = "tender"
        private const val AWARDS_BUSINESS_FUNCTIONS_PATH = "awards.requirementResponses.responder"
        private const val SUBMISSIONS_BUSINESS_FUNCTIONS_PATH = "submissions.details[*].candidates[*].persones[*]"
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val entities = parameters.entities

        val tender = context.tender
        val tenderDocuments: List<DocumentId> = getTenderDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val amendmentDocuments: List<DocumentId> = getAmendmentDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val awardRequirementResponseDocuments: List<DocumentId> =
            getAwardRequirementResponseDocumentsIds(context, entities)
                .orForwardFail { fail -> return fail }

        val submissionsDocuments: List<DocumentId> =
            getSubmissionsDocumentsIds(context, entities)
                .orForwardFail { fail -> return fail }

        val submissionCandidateDocuments: List<DocumentId> =
            getSubmissionsCandidateDocumentsIds(context, entities)
                .orForwardFail { fail -> return fail }

        val documentIds: List<DocumentId> = tenderDocuments.plus(amendmentDocuments)
            .plus(awardRequirementResponseDocuments)
            .plus(submissionsDocuments)
            .plus(submissionCandidateDocuments)

        if (documentIds.isEmpty())
            return success(Reply.None)

        return client.checkRegistration(
            id = commandId,
            params = CheckRegistrationAction.Params(
                ids = documentIds
            )
        )
    }

    private fun getTenderDocumentsIds(
        tender: Tender?,
        entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.TENDER]) {
            EntityValue.OPTIONAL -> getTenderDocumentsIdsOptional(tender)
            EntityValue.REQUIRED -> getTenderDocumentsIdsRequired(tender)
                .doOnError { error -> return error.asFailure() }
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
                .doOnError { error -> return error.asFailure() }
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
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD_REQUIREMENT_RESPONSE]) {
            EntityValue.OPTIONAL -> getAwardDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getAwardDocumentsIdsRequired(context)
                .doOnError { error -> return error.asFailure() }
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getSubmissionsDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.SUBMISSION]) {
            EntityValue.OPTIONAL -> getSubmissionsDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getSubmissionsDocumentsIdsRequired(context)
                .doOnError { error -> return error.asFailure() }
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getSubmissionsDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.submissions.details
            .getOrNull(0)
            ?.documents
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()


    private fun getSubmissionsDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.submissions.getDetailsIfOnlyOne()
            .orForwardFail { fail -> return fail }
            .getDocumentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { document -> document.id }
            .asSuccess()

    private fun getSubmissionsCandidateDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.SUBMISSION_CANDIDATE]) {
            EntityValue.OPTIONAL -> getSubmissionsCandidateDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getSubmissionsCandidateDocumentsIdsRequired(context)
                .doOnError { error -> return error.asFailure() }
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getSubmissionsCandidateDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.submissions.details
            .getOrNull(0)
            ?.candidates
            ?.asSequence()
            ?.flatMap { candidate -> candidate.persons.asSequence() }
            ?.flatMap { persone -> persone.businessFunctions.asSequence() }
            ?.flatMap { businessFunction -> businessFunction.documents.asSequence() }
            ?.map { document -> document.id }
            ?.toList()
            .orEmpty()
            .asSuccess()

    private fun getSubmissionsCandidateDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.submissions.getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { submission ->
                submission.getCandidatesIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .flatMap { candidate ->
                candidate.getPersonesIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .flatMap { persone ->
                persone.getBusinessFunctionsIfNotEmpty(path = SUBMISSIONS_BUSINESS_FUNCTIONS_PATH)
                    .orForwardFail { fail -> return fail }
            }
            .flatMap { businessFunction ->
                businessFunction.getDocumentsIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getAwardDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.awards
            .asSequence()
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

    private fun getAwardDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.getAwardsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .flatMap { award ->
                award.getRequirementResponseIfOnlyOne()
                    .orForwardFail { fail -> return fail }
                    .getResponder()
                    .orForwardFail { fail -> return fail }
                    .getBusinessFunctionsIfNotEmpty(path = AWARDS_BUSINESS_FUNCTIONS_PATH)
                    .orForwardFail { fail -> return fail }
                    .flatMap { businessFunction ->
                        businessFunction.getDocumentsIfNotEmpty()
                            .orForwardFail { fail -> return fail }
                    }
            }
            .map { document -> document.id }
            .asSuccess()

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    class Parameters(
        val entities: Map<EntityKey, EntityValue>
    )

    enum class EntityKey(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        TENDER("tender"),
        SUBMISSION("submission"),
        SUBMISSION_CANDIDATE("submission.candidate");

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
