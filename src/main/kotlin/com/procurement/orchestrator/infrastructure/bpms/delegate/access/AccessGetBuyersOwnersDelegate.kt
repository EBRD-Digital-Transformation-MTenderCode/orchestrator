package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.masterdata.ProcessMasterData
import com.procurement.orchestrator.domain.model.masterdata.access.Access
import com.procurement.orchestrator.domain.model.masterdata.access.Buyers
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetBuyersOwnersAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessGetBuyersOwnersDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetBuyersOwnersAction.Result>(
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
    ): Result<Reply<GetBuyersOwnersAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo

        return accessClient.getBuyersOwners(
            id = commandId,
            params = GetBuyersOwnersAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetBuyersOwnersAction.Result>
    ): MaybeFail<Fail.Incident> {
        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.GetBuyersOwners
                )
            )

        val accessBuyers = data.buyers
            .map { it.toDomain() }
            .let { Buyers(it) }

        val processMasterData = ProcessMasterData(access = Access(buyers = accessBuyers))
        context.processMasterData = processMasterData

        return MaybeFail.none()
    }
}
