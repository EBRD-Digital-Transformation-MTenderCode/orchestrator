package com.procurement.orchestrator.infrastructure.bpms.delegate.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.RequisitionCommands
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindCriteriaAndTargetsForPacsAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class RequistionFindCriteriaAndTargetsForPacsDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindCriteriaAndTargetsForPacsAction.Result>(
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
    ): Result<Reply<FindCriteriaAndTargetsForPacsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { return it }

        return requisitionClient.findCriteriaAndTargetsForPacs(
            id = commandId,
            params = FindCriteriaAndTargetsForPacsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = FindCriteriaAndTargetsForPacsAction.Params.Tender(tender.lots.map { lot ->
                    FindCriteriaAndTargetsForPacsAction.Params.Tender.Lot(lot.id)
                })
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindCriteriaAndTargetsForPacsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.REQUISITION,
                    action = RequisitionCommands.FindCriteriaAndTargetsForPacs
                )
            )

        val tenderReceived = data.tender?.toDomain()
        if (tenderReceived != null) {
            context.tender = context.tender?.updateBy(tenderReceived) ?: tenderReceived
        }

        return MaybeFail.none()
    }
}
