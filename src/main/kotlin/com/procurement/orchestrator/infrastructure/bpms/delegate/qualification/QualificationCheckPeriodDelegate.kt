package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import org.springframework.stereotype.Component

@Component
class QualificationCheckPeriodDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
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

        return qualificationClient.checkPeriod(
            id = commandId,
            params = CheckPeriodAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                date = requestInfo.timestamp
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident> = MaybeFail.none()
}
