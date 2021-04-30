package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getConfirmationResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckAccessToRequestOfConfirmationAction.Params
import org.springframework.stereotype.Component

@Component
class ContractingCheckAccessToRequestOfConfirmation(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        val contract = context.getContractIfOnlyOne()
            .orForwardFail { return it }
            .let { contract ->
                Params.Contract(
                    id = contract.id,
                    confirmationResponses = contract.getConfirmationResponseIfOnlyOne()
                        .orForwardFail { return it }
                        .let { confirmationResponse ->
                            Params.Contract.ConfirmationResponse(
                                id = confirmationResponse.id,
                                requestId = confirmationResponse.requestId
                            )
                        }
                        .let { listOf(it) }
                )
            }
        return contractingClient.checkAccessToRequestOfConfirmation(
            id = commandId,
            params = Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                token = requestInfo.token!!,
                owner = requestInfo.owner,
                contracts = listOf(contract)
            )
        )
    }
}
