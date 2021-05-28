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
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetOrganizationIdAndSourceOfRequestGroupAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetSupplierIdsByContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetSupplierIdsByContractAction.Params
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingGetSupplierIdsByContractDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetSupplierIdsByContractAction.Result>(
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
    ): Result<Reply<GetSupplierIdsByContractAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contracts = context.contracts
            .map { contract ->
                Params.Contract(
                    id = contract.id
                )
            }
        return contractingClient.getSuppliersIdsByContract(
            id = commandId,
            params = Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                contracts = contracts
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetSupplierIdsByContractAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.CONTRACTING,
                    action = ContractingCommands.GetSuppliersIdsByContract
                )
            )

        val parties = data.contracts
            .flatMap { contract ->
                contract.suppliers
                    .map { supplier ->
                        Party(id = supplier.id)
                    }
            }
            .let { Parties(it) }

        context.parties = parties

        return MaybeFail.none()
    }
}