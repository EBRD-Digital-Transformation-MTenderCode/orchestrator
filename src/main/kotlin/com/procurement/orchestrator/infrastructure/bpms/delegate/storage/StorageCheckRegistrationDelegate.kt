package com.procurement.orchestrator.infrastructure.bpms.delegate.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
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
import com.procurement.orchestrator.domain.model.document.DocumentId
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

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val entities: List<Entity> = parameterContainer.getListString("entities")
            .orReturnFail { return failure(it) }
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
        val tenderDocuments: List<DocumentId> = if (Entity.TENDER in entities)
            tender?.documents
                ?.asSequence()
                ?.map { document -> document.id }
                ?.toList()
                .orEmpty()
        else
            emptyList()

        val amendmentDocuments: List<DocumentId> = if (Entity.AMENDMENT in entities)
            tender?.amendments
                ?.asSequence()
                ?.flatMap { amendment -> amendment.documents.asSequence() }
                ?.map { document -> document.id }
                ?.toList()
                .orEmpty()
        else
            emptyList()

        val awardRequirementResponseDocuments: List<DocumentId> = if (Entity.AWARD_REQUIREMENT_RESPONSE in entities)
            context.awards
                .asSequence()
                .flatMap { award ->
                    award.requirementResponses.asSequence()
                }
                .flatMap { requirementResponse ->
                    requirementResponse.responder?.businessFunctions?.asSequence() ?: emptySequence()
                }
                .flatMap { businessFunction ->
                    businessFunction.documents.asSequence()
                }
                .map { document -> document.id }
                .toList()
        else
            emptyList()

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
