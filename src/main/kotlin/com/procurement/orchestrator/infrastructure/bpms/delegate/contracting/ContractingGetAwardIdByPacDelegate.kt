package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetAwardIdByPacAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingGetAwardIdByPacDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetAwardIdByPacAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Unit.asSuccess()

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetAwardIdByPacAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contract = context.contracts.first()

        return contractingClient.getAwardIdByPac(
            id = commandId,
            params = GetAwardIdByPacAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                contracts = listOf(
                    GetAwardIdByPacAction.Params.Contract(
                        id = contract.id
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetAwardIdByPacAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.CONTRACTING,
                    action = ContractingCommands.GetAwardIdByPac
                )
            )

        val receivedContracts = data.contracts
            .map { it.toDomain() }
            .let { Contracts(it) }

        context.contracts = context.contracts updateBy receivedContracts

        return MaybeFail.none()
    }
}
