package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Outcomes
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
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingCreateFrameworkContractDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateFrameworkContractAction.Result>(
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
    ): Result<Reply<CreateFrameworkContractAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        return contractingClient.createFrameworkContract(
            id = commandId,
            params = CreateFrameworkContractAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                date = requestInfo.timestamp,
                owner = requestInfo.owner
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateFrameworkContractAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(ExternalServiceName.CONTRACTING, ContractingCommands.CreateFrameworkContract)
            )

        val receivedContracts = data.contracts
            .map { CreateFrameworkContractAction.ResponseConverter.Contract.toDomain(it) }
            .let { Contracts(it) }

        context.contracts = receivedContracts
        context.outcomes = createOutcomes(context, data.contracts,  data.token)

        return MaybeFail.none()
    }

    private fun createOutcomes(context: CamundaGlobalContext, contracts: List<CreateFrameworkContractAction.Result.Contact>, token: Token): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = contracts
            .map { contract -> Outcomes.Details.Contract(id = contract.id, token = token) }

        val updatedDetails = details.copy(contracts = newOutcomes)
        outcomes[platformId] = updatedDetails
        return outcomes
    }

}
