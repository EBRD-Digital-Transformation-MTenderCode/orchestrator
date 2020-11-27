package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotsValueAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessGetLotsValueDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetLotsValueAction.Result>(
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
    ): Result<Reply<GetLotsValueAction.Result>, Fail.Incident> {

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        return accessClient.getLotsValue(
            id = commandId,
            params = GetLotsValueAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = tender.lots
                    .map { lot -> GetLotsValueAction.Params.Tender.Lot(lot.id) }
                    .let { GetLotsValueAction.Params.Tender(it) }

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetLotsValueAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.ACCESS, action = AccessCommands.GetLotsValue)
            )

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val receivedLots = data.tender.lots
            .map { lot -> lot.toDomain() }
            .let { Lots(it) }

        val updatedLots = tender.lots updateBy receivedLots
        val updatedTender = tender.copy(
            lots = updatedLots
        )

        context.tender = updatedTender

        return MaybeFail.none()
    }
}