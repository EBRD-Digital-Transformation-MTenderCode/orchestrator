package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.procurement.orchestrator.application.client.NotificatorClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.BpeIncident
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class NotifierBpeIncidentDelegate(
    logger: Logger,
    private val incidentNotificatorClient: NotificatorClient<BpeIncident>,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(context: CamundaGlobalContext, parameters: Unit): Result<Reply<Unit>, Fail.Incident> {
        //TODO
        val incident = BpeIncident(
            id = "",
            date = nowDefaultUTC(),
            level = BpeIncident.Level.ERROR,
            service = BpeIncident.Service(
                id = GlobalProperties.service.id,
                name = GlobalProperties.service.name,
                version = GlobalProperties.service.version
            ),
            details = listOf(
                BpeIncident.Detail(
                    code = "",
                    description = ""
                )
            )
        )

        incidentNotificatorClient.send(incident)
            .doOnFail { return failure(it) }
        return success(Reply.None)
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
