package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.delegate.kafka.MessageProducer
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType
import com.procurement.orchestrator.domain.dto.command.CommandMessage
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import org.springframework.stereotype.Component

@Component
class BpeSendMessageToDocGeneratorDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    private val transform: Transform,
    private val messageProducer: MessageProducer
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val contracts = context.contracts

        val data = BpeSendMessageToDocGeneratorAction.Data(
            country = requestInfo.country,
            language = requestInfo.language,
            cpid = processInfo.cpid!!,
            ocid = processInfo.ocid!!,
            operationId = requestInfo.operationId,
            startDate = requestInfo.timestamp,
            contracts = contracts.firstOrNull()?.internalId
                ?.let { listOf(BpeSendMessageToDocGeneratorAction.Data.Contract(internalId = it)) }
        )
        val serializedData = transform.tryToJsonNode(data)
            .orForwardFail { fail -> return fail }
        sendToDocGenerator(serializedData)
            .orForwardFail { fail -> return fail }

        return Result.success(Reply.None)
    }

    private fun sendToDocGenerator(
        serializedData: JsonNode
    ): Result<Unit, Fail.Incident.Bus.Producer> {
        val commandMessage = CommandMessage(
            "",
            DocGeneratorCommandType.GENERATE_FC_DOC,
            null,
            serializedData,
            CommandMessage.ApiVersion.V_0_0_1
        )
        return try {
            messageProducer.sendToDocGenerator(commandMessage)
            Unit.asSuccess()
        } catch (exception: Exception) {
            Result.failure(
                Fail.Incident.Bus.Producer(
                    description = "Cannot send command message to doc generator. Command message = '${commandMessage}'",
                    exception = exception
                )
            )
        }
    }
}
