package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.ResponseId
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asOption
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class NotifierErrorNotificationToPlatformDelegate(
    logger: Logger,
    private val platformNotificatorClient: NotificatorClient<PlatformNotification.MessageEnvelop>,
    operationStepRepository: OperationStepRepository,
    private val transform: Transform
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(context: CamundaGlobalContext, parameters: Unit): Result<Option<Unit>, Fail.Incident> {
        buildMessages(context)
            .orReturnFail { return failure(it) }
            .also { message ->
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

    private fun buildMessages(context: CamundaGlobalContext): Result<Option<PlatformNotification.MessageEnvelop>, Fail.Incident> {
        val errors = context.errors
            ?: return success(Option.none())

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo
        val message = PlatformNotification.Message(
            responseId = ResponseId.generate(),
            operationId = requestInfo.operationId,
            initiator = initiator(processInfo.operationType),
            body = PlatformNotification.Message.Body.Errors(
                items = errors.map { error ->
                    PlatformNotification.Message.Body.Errors.Error(
                        code = error.code,
                        description = error.description,
                        details = error.details
                            .map { detail ->
                                PlatformNotification.Message.Body.Errors.Error.Detail(
                                    id = detail.id,
                                    name = detail.name
                                )
                            }
                    )
                }
            )
        )
        return PlatformNotification.MessageEnvelop(
            platformId = requestInfo.platformId,
            operationId = requestInfo.operationId,
            message = transform.trySerialization(message)
                .orReturnFail { return failure(it) }
        )
            .asOption()
            .asSuccess<Option<PlatformNotification.MessageEnvelop>, Fail.Incident>()
    }
}
