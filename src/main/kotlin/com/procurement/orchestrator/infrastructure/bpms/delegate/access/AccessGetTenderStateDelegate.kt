package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderStateAction
import org.springframework.stereotype.Component

@Component
class AccessGetTenderStateDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetTenderStateAction.Result>(
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
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.getTenderState(
            id = commandId,
            params = GetTenderStateAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: GetTenderStateAction.Result
    ): MaybeFail<Fail.Incident>{

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedTender = tender.copy(
            status = data.status,
            statusDetails = data.statusDetails
        )
        context.tender = updatedTender

        return MaybeFail.none()
    }
}
