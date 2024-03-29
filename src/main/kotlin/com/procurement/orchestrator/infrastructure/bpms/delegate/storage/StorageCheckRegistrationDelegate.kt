package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getAwardsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getBidsDetailIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getCandidatesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getConfirmationResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getConfirmationResponsesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getContractIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getPersonesIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getQualificationBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getQualificationDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getQualificationIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
import com.procurement.orchestrator.application.model.context.extension.getSubmissionBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getSubmissionDocumentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getSuppliersIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
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

        val qualificationDocuments: List<DocumentId> =
            getQualificationDocumentsIds(context, entities)
                .orForwardFail { fail -> return fail }

        val qualificationReqRsDocuments: List<DocumentId> =
            getQualificationReqRsDocumentsIds(context, entities)
                .orForwardFail { fail -> return fail }

        val bidsDocuments: List<DocumentId> = getBidsDocumentsIds(context, entities)
            .orForwardFail { fail -> return fail }

        val bidTenderersDocuments: List<DocumentId> = getBidsTendererDocumentsIds(context, entities)
            .orForwardFail { fail -> return fail }

        val awardDocuments: List<DocumentId> = getAwardsDocumentsIds(context, entities)
            .orForwardFail { fail -> return fail }

        val awardSuppliersDocuments: List<DocumentId> = getAwardsSuppliersDocumentsIds(context, entities)
            .orForwardFail { fail -> return fail }

        val contractConfirmationResponseDocuments: List<DocumentId> = getContractConfirmationResponseDocuments(context, entities)
            .orForwardFail { fail -> return fail }

        val documentIds: List<DocumentId> = tenderDocuments.asSequence()
            .plus(amendmentDocuments)
            .plus(awardRequirementResponseDocuments)
            .plus(submissionsDocuments)
            .plus(submissionCandidateDocuments)
            .plus(qualificationDocuments)
            .plus(qualificationReqRsDocuments)
            .plus(bidsDocuments)
            .plus(bidTenderersDocuments)
            .plus(awardDocuments)
            .plus(awardSuppliersDocuments)
            .plus(contractConfirmationResponseDocuments)
            .toList()

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
            EntityValue.OPTIONAL -> getAwardRequirementRsDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getAwardRequirementRsDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getSubmissionsDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.SUBMISSION]) {
            EntityValue.OPTIONAL -> getSubmissionsDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getSubmissionsDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getSubmissionsDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.submissions
            ?.details
            ?.getOrNull(0)
            ?.documents
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()

    private fun getSubmissionsDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfOnlyOne()
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
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getSubmissionsCandidateDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.submissions
            ?.details
            ?.getOrNull(0)
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
        context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
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
                persone.getSubmissionBusinessFunctionsIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .flatMap { businessFunction ->
                businessFunction.getSubmissionDocumentsIfNotEmpty()
                    .orForwardFail { fail -> return fail }
            }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getAwardRequirementRsDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
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

    private fun getAwardRequirementRsDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.getAwardsIfNotEmpty()
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
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.QUALIFICATION_REQUIREMENT_RESPONSE]) {
            EntityValue.OPTIONAL -> getQualificationReqRsDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getQualificationReqRsDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getQualificationReqRsDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.qualifications
            .asSequence()
            .flatMap { qualification -> qualification.requirementResponses.asSequence() }
            .flatMap { requirementResponse -> requirementResponse.responder?.businessFunctions?.asSequence() ?: emptySequence() }
            .flatMap { businessFunction -> businessFunction.documents.asSequence() }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getQualificationReqRsDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.getQualificationIfOnlyOne()
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
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.QUALIFICATION]) {
            EntityValue.OPTIONAL -> getQualificationDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getQualificationDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getQualificationDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.qualifications
            .getOrNull(0)
            ?.documents
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()

    private fun getQualificationDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.getQualificationIfOnlyOne()
            .orForwardFail { fail -> return fail }
            .getDocumentsIfNotEmpty()
            .orForwardFail { fail -> return fail }
            .map { document -> document.id }
            .toList()
            .asSuccess()

    private fun getBidsDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.BID]) {
            EntityValue.OPTIONAL -> getBidsDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getBidsDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getBidsDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.bids?.details?.getOrNull(0)
            ?.documents
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()

    private fun getBidsDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.bids.getBidsDetailIfOnlyOne()
            .orForwardFail { fail -> return fail }
            .documents
            .map { document -> document.id }
            .asSuccess()

    private fun getBidsTendererDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.BID_TENDERER]) {
            EntityValue.OPTIONAL -> getBidsTendererDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getBidsTendererDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getBidsTendererDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.bids?.details?.getOrNull(0)
            ?.tenderers
            ?.flatMap { tenderer -> tenderer.persons }
            ?.flatMap { person -> person.businessFunctions }
            ?.flatMap { businessFunction -> businessFunction.documents }
            ?.map { document -> document.id }
            .orEmpty()
            .asSuccess()

    private fun getBidsTendererDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.bids.getBidsDetailIfOnlyOne()
            .orForwardFail { fail -> return fail }
            .tenderers
            .flatMap { tenderer -> tenderer.persons }
            .flatMap { person -> person.businessFunctions }
            .flatMap { businessFunction -> businessFunction.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getAwardsDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD]) {
            EntityValue.OPTIONAL -> getAwardsDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getAwardsDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAwardsDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.awards.flatMap { award -> award.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getAwardsDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.getAwardsIfNotEmpty().orForwardFail { return it }
            .flatMap { award -> award.getDocumentsIfNotEmpty().orForwardFail { return it } }
            .map { document -> document.id }
            .asSuccess()

    private fun getAwardsSuppliersDocumentsIds(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.AWARD_SUPPLIER]) {
            EntityValue.OPTIONAL -> getAwardsSuppliersDocumentIdsOptional(context)
            EntityValue.REQUIRED -> getAwardsSuppliersDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getAwardsSuppliersDocumentIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context.awards.flatMap { award -> award.suppliers }
            .flatMap { supplier -> supplier.persons }
            .flatMap { person -> person.businessFunctions }
            .flatMap { businessFunction -> businessFunction.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getAwardsSuppliersDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> {
        val businessFunctionsPath = "awards.suppliers.persones"
        val documentsPath = "$businessFunctionsPath.businessFunctions"

        return context.getAwardsIfNotEmpty()
            .orForwardFail { return it }
            .flatMap { award -> award.getSuppliersIfNotEmpty().orForwardFail { return it } }
            .flatMap { supplier -> supplier.getPersonesIfNotEmpty().orForwardFail { return it } }
            .flatMap { person -> person.getBusinessFunctionsIfNotEmpty(businessFunctionsPath).orForwardFail { return it } }
            .flatMap { businessFunction -> businessFunction.getDocumentsIfNotEmpty(documentsPath).orForwardFail { return it } }
            .map { document -> document.id }
            .asSuccess()
    }

    private fun getContractConfirmationResponseDocuments(
        context: CamundaGlobalContext, entities: Map<EntityKey, EntityValue>
    ): Result<List<DocumentId>, Fail.Incident> =
        when (entities[EntityKey.CONTRACT_CONFIRMATION_RESPONSE]) {
            EntityValue.OPTIONAL -> getContractConfirmationResponseDocumentsIdsOptional(context)
            EntityValue.REQUIRED -> getContractConfirmationResponseDocumentsIdsRequired(context)
            null -> emptyList<DocumentId>().asSuccess()
        }

    private fun getContractConfirmationResponseDocumentsIdsOptional(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> =
        context
            .getContractIfOnlyOne().orForwardFail { return it }
            .getConfirmationResponseIfOnlyOne().orForwardFail { return it }
            .relatedPerson
            ?.businessFunctions.orEmpty()
            .flatMap { businessFunction -> businessFunction.documents }
            .map { document -> document.id }
            .asSuccess()

    private fun getContractConfirmationResponseDocumentsIdsRequired(context: CamundaGlobalContext): Result<List<DocumentId>, Fail.Incident> {
        val relatedPersonPath = "contracts.confirmationResponses.relatedPerson"

        val relatedPerson = context
            .getContractIfNotEmpty().orForwardFail { return it }.first()
            .getConfirmationResponsesIfNotEmpty().orForwardFail { return it }.first()
            .relatedPerson
            ?: return Fail.Incident.Bpms.Context.Missing(name = relatedPersonPath).asFailure()

        return relatedPerson.businessFunctions
            .flatMap { businessFunction -> businessFunction.documents }
            .map { document -> document.id }
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
        BID_TENDERER("bid.tenderer"),
        CONTRACT_CONFIRMATION_RESPONSE("contract.confirmationResponse"),
        QUALIFICATION("qualification"),
        QUALIFICATION_REQUIREMENT_RESPONSE("qualification.requirementResponse"),
        SUBMISSION("submission"),
        SUBMISSION_CANDIDATE("submission.candidate"),
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
