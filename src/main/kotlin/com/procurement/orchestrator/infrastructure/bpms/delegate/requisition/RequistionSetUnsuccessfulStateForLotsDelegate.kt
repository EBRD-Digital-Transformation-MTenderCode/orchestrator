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
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.RequisitionCommands
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.SetUnsuccessfulStateForLotsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class RequistionSetUnsuccessfulStateForLotsDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, SetUnsuccessfulStateForLotsAction.Result>(
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
    ): Result<Reply<SetUnsuccessfulStateForLotsAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return requisitionClient.setUnsuccessfulStateForLots(
            id = commandId,
            params = SetUnsuccessfulStateForLotsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = tender.toParams()
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<SetUnsuccessfulStateForLotsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.REQUISITION,
                    action = RequisitionCommands.SetUnsuccessfulStateForLots
                )
            )

        val receivedLots = data.tender.lots
            .map { it.toDomain() }
            .let { Lots(it) }

        val updatedLots = context.tender!!.lots.updateBy(receivedLots)
        context.tender = context.tender!!.copy(lots = updatedLots)

        return MaybeFail.none()
    }

    private fun Tender.toParams(): SetUnsuccessfulStateForLotsAction.Params.Tender {
        val convertedLots = this.lots.map { it.toParams() }
        return SetUnsuccessfulStateForLotsAction.Params.Tender(lots = convertedLots)
    }

    private fun Lot.toParams(): SetUnsuccessfulStateForLotsAction.Params.Tender.Lot =
        SetUnsuccessfulStateForLotsAction.Params.Tender.Lot(id = id)

    private fun SetUnsuccessfulStateForLotsAction.Result.Tender.Lot.toDomain(): Lot =
        Lot(
            id = id,
            status = status,
            statusDetails = statusDetails
        )
}
