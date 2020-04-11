package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.ResponseId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.configuration.property.UriProperties
import org.springframework.stereotype.Component

@Component
class NotifierSuccessNotificationToPlatformDelegate(
    logger: Logger,
    private val platformNotificatorClient: NotificatorClient<PlatformNotification.MessageEnvelop>,
    operationStepRepository: OperationStepRepository,
    private val transform: Transform,
    uriProperties: UriProperties
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    private val budgetUri = uriProperties.tender
        .let { uri ->
            if (uriProperties.tender.endsWith("/"))
                uri.substring(0, uri.length - 1)
            else
                uri
        }

    private val tenderUri = uriProperties.tender
        .let { uri ->
            if (uriProperties.tender.endsWith("/"))
                uri.substring(0, uri.length - 1)
            else
                uri
        }

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(context: CamundaGlobalContext, parameters: Unit): Result<Option<Unit>, Fail.Incident> {
        buildMessages(context)
            .orForwardFail { fail -> return fail }
            .forEach { message ->
                platformNotificatorClient.send(message)
                    .doOnFail { return failure(it) }
            }

        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    private fun buildMessages(context: CamundaGlobalContext): Result<List<PlatformNotification.MessageEnvelop>, Fail.Incident> {
        val outcomes = context.outcomes
            ?: return success(emptyList())

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        return outcomes.map { (platformId, detail) ->
            val message = PlatformNotification.Message(
                responseId = ResponseId.generate(),
                operationId = requestInfo.operationId,
                initiator = initiator(processInfo.operationType),
                body = PlatformNotification.Message.Body.Success(
                    ocid = processInfo.ocid,
                    url = generateUrl(processInfo.operationType, cpid = processInfo.cpid, ocid = processInfo.ocid),
                    operationDate = requestInfo.timestamp,
                    outcomes = buildOutcomes(detail)
                )
            )

            val serializedMessage = transform.trySerialization(message)
                .orForwardFail { fail -> return fail }

            PlatformNotification.MessageEnvelop(
                platformId = platformId,
                operationId = requestInfo.operationId,
                message = serializedMessage
            )
        }
            .asSuccess<List<PlatformNotification.MessageEnvelop>, Fail.Incident>()
    }

    private fun buildOutcomes(details: Outcomes.Details) = PlatformNotification.Outcomes(
        amendments = details.amendments
            .map { amendment ->
                PlatformNotification.Outcomes.Amendment(
                    id = amendment.id,
                    token = amendment.token
                )
            }
    )

    private fun generateUrl(operationType: OperationTypeProcess, cpid: Cpid, ocid: Ocid): String =
        when (operationType) {
            OperationTypeProcess.TENDER_CANCELLATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.LOT_CANCELLATION -> "$tenderUri/$cpid/$ocid"
            OperationTypeProcess.DECLARE_NON_CONFLICT_OF_INTEREST -> "$tenderUri/$cpid/$ocid"
        }
}
