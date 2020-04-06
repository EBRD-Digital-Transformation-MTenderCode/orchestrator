package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class NotifierBpeIncidentDelegate(
    logger: Logger,
    private val incidentNotificatorClient: NotificatorClient<Incident>,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(context: CamundaGlobalContext, parameters: Unit): Result<Option<Unit>, Fail.Incident> {
        incidentNotificatorClient.send(context.incident!!)
            .doOnFail { return failure(it) }
        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
