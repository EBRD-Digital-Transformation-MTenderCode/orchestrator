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
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingSetStateForContractsDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, SetStateForContractsAction.Result>(
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
    ): Result<Reply<SetStateForContractsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo


        return contractingClient.setStateForContracts(
            id = commandId,
            params = SetStateForContractsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                country = requestInfo.country,
                pmd = processInfo.pmd,
                operationType = processInfo.operationType,
                tender = context.tender
                    ?.let { tender ->
                        SetStateForContractsAction.Params.Tender(
                            tender.lots.map { lot ->
                                SetStateForContractsAction.Params.Tender.Lot(lot.id)
                            }
                        )
                    },
                contracts = context.contracts.map { contract ->
                    SetStateForContractsAction.Params.Contract(contract.id)
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<SetStateForContractsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(ExternalServiceName.CONTRACTING, ContractingCommands.SetStateForContracts)
            )

        val receivedContracts = data.contracts
            .map { SetStateForContractsAction.ResponseConverter.Contract.toDomain(it) }
            .let { Contracts(it) }

        context.contracts = context.contracts updateBy receivedContracts

        return MaybeFail.none()
    }
}
