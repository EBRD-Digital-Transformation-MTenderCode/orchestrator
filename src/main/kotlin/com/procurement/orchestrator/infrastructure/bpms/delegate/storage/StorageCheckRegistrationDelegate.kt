package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getBusinessFunctionsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getResponder
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
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
        private const val TENDER_NAME = "tender"
        private const val BUSINESS_FUNCTIONS_PATH = "awards.requirementResponses.responder.businessFunctions"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val entities: List<Entity> = parameterContainer.getListString("entities")
            .orForwardFail { fail -> return fail }
            .map {
                Entity.orNull(it)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "entities",
                            expectedValues = OperationTypeProcess.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return success(Parameters(entities = entities))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<Unit>, Fail.Incident> {

        val entities = parameters.entities.toSet()

        val tender = context.tender
        val tenderDocuments: List<DocumentId> = getTenderDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val amendmentDocuments: List<DocumentId> = getAmendmentDocumentsIds(tender, entities)
            .orForwardFail { fail -> return fail }

        val awardRequirementResponseDocuments: List<DocumentId> =
            getAwardRequirementResponseDocumentsIds(context, entities)
                .orForwardFail { fail -> return fail }

        val documentIds: List<DocumentId> = tenderDocuments + amendmentDocuments + awardRequirementResponseDocuments
        if (documentIds.isEmpty())
            return success(Reply.None)

        val requestInfo = context.requestInfo
        return client.checkRegistration(
            id = commandId,
            params = CheckRegistrationAction.Params(
                datePublished = requestInfo.timestamp,
                ids = documentIds
            )
        )
    }

    private fun getTenderDocumentsIds(tender: Tender?, entities: Set<Entity>): Result<List<DocumentId>, Fail.Incident> {
        return if (Entity.TENDER in entities) {
            if (tender == null)
                return failure(Fail.Incident.Bpms.Context.Missing(name = TENDER_NAME))

            tender.documents
                .map { document -> document.id }
                .asSuccess()
        } else
            emptyList<DocumentId>().asSuccess()
    }

    private fun getAmendmentDocumentsIds(
        tender: Tender?, entities: Set<Entity>
    ): Result<List<DocumentId>, Fail.Incident> {

        return if (Entity.AMENDMENT in entities) {
            if (tender == null)
                return failure(Fail.Incident.Bpms.Context.Missing(name = TENDER_NAME))

            tender.getAmendmentIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .documents
                .map { document -> document.id }
                .asSuccess()
        } else
            emptyList<DocumentId>().asSuccess()
    }

    private fun getAwardRequirementResponseDocumentsIds(
        context: CamundaGlobalContext, entities: Set<Entity>
    ): Result<List<DocumentId>, Fail.Incident> {

        return if (Entity.AWARD_REQUIREMENT_RESPONSE in entities) {
            context.getAwardIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .getRequirementResponseIfOnlyOne()
                .orForwardFail { fail -> return fail }
                .getResponder()
                .orForwardFail { fail -> return fail }
                .getBusinessFunctionsIfNotEmpty(path = BUSINESS_FUNCTIONS_PATH)
                .orForwardFail { fail -> return fail }
                .asSequence()
                .flatMap { businessFunction ->
                    businessFunction.documents.asSequence()
                }
                .map { document -> document.id }
                .toList()
                .asSuccess()
        } else
            emptyList<DocumentId>().asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    class Parameters(
        val entities: List<Entity>
    )

    enum class Entity(override val key: String) : EnumElementProvider.Key {

        AMENDMENT("amendment"),
        AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
        TENDER("tender");

        override fun toString(): String = key

        companion object : EnumElementProvider<Entity>(info = info())
    }
}
