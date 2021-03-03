package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.delegate.kafka.MessageProducer
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.commands.DocGeneratorCommandType
import com.procurement.orchestrator.domain.dto.command.CommandMessage
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
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
) : AbstractExternalDelegate<BpeSendMessageToDocGeneratorDelegate.Parameters, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer
            .getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let {
                Location.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return success(Parameters(location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        val operationId = requestInfo.operationId

        val objectId = defineObjectId(context, parameters.location)

        val data = BpeSendMessageToDocGeneratorAction.Data(
            country = requestInfo.country,
            language = requestInfo.language,
            cpid = processInfo.cpid!!,
            ocid = processInfo.ocid!!,
            operationId = requestInfo.operationId,
            startDate = requestInfo.timestamp,
            pmd = processInfo.pmd,
            documentInitiator = processInfo.operationType,
            objectId = objectId
        )
        val serializedData = transform.tryToJsonNode(data)
            .orForwardFail { fail -> return fail }
        sendToDocGenerator(serializedData, operationId)
            .orForwardFail { fail -> return fail }

        return success(Reply.None)
    }

    private fun sendToDocGenerator(serializedData: JsonNode, operationId: OperationId): Result<Unit, Fail.Incident.Bus.Producer> {
        val commandMessage = CommandMessage(
            operationId.toString(),
            DocGeneratorCommandType.GENERATE_DOCUMENT,
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

    private fun defineObjectId(context: CamundaGlobalContext, location: Location): String =
        when (location) {
            Location.CONTRACT -> context.contracts.first().id.toString()
        }

    class Parameters(val location: Location)

    enum class Location(override val key: String) : EnumElementProvider.Key {
        CONTRACT("contract");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
