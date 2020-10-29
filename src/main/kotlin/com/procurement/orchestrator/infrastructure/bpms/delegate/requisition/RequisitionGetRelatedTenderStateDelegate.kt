package com.procurement.orchestrator.infrastructure.bpms.delegate.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.RequisitionCommands
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class RequisitionGetRelatedTenderStateDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetTenderStateAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident> {

        val relatedProcess = context.processInfo.relatedProcess

        return requisitionClient.getTenderState(
            id = commandId,
            params = GetTenderStateAction.Params(
                cpid = relatedProcess?.cpid,
                ocid = relatedProcess?.ocid
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetTenderStateAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.REQUISITION,
                    action = RequisitionCommands.GetTenderState
                )
            )

        val updatedTender = context.tender?.copy(status = data.status, statusDetails = data.statusDetails)
            ?: Tender(status = data.status, statusDetails = data.statusDetails)


        context.tender = updatedTender

        return MaybeFail.none()
    }
}
