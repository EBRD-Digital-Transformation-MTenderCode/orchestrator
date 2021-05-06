package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequest
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequests
import com.procurement.orchestrator.domain.model.party.Parties
import com.procurement.orchestrator.domain.model.party.Party
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetOrganizationIdAndSourceOfRequestGroupAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingGetOrganizationIdAndSourceOfRequestGroupDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetOrganizationIdAndSourceOfRequestGroupAction.Result>(
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
    ): Result<Reply<GetOrganizationIdAndSourceOfRequestGroupAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contract = context.getContractIfOnlyOne()
            .orForwardFail { return it }

        return contractingClient.getOrganizationIdAndSourceOfRequestGroup(
            id = commandId,
            params = GetOrganizationIdAndSourceOfRequestGroupAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                contracts = GetOrganizationIdAndSourceOfRequestGroupAction.Params.Contract(
                    id = contract.id,
                    confirmationResponses = contract.confirmationResponses.map { confirmationResponse ->
                        GetOrganizationIdAndSourceOfRequestGroupAction.Params.Contract.ConfirmationResponse(
                            id = confirmationResponse.id,
                            requestGroup = confirmationResponse.requestId
                        )
                    }
                ).let { listOf(it) }

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetOrganizationIdAndSourceOfRequestGroupAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.CONTRACTING,
                    action = ContractingCommands.GetOrganizationIdAndSourceOfRequestGroup
                )
            )

        val confirmationRequest = data.contracts.first().confirmationRequests.first()
        val updatedContract = getUpdatedContract(confirmationRequest, context)
            .orForwardFail { return it.asMaybeFail }

        val organizationId = confirmationRequest.requestGroups.first().relatedOrganization.id
        val parties = Parties(listOf(Party(id = organizationId)))

        context.parties = parties
        context.contracts = Contracts(listOf(updatedContract))

        return MaybeFail.none()
    }

    private fun getUpdatedContract(
        confirmationRequest: GetOrganizationIdAndSourceOfRequestGroupAction.Result.Contract.ConfirmationRequest,
        context: CamundaGlobalContext
    ): Result<Contract, Fail.Incident> {
        val confirmationRequests = ConfirmationRequests(
            listOf(
                ConfirmationRequest(
                    id = confirmationRequest.id,
                    source = confirmationRequest.source
                )
            )
        )

        return context.getContractIfOnlyOne()
            .orForwardFail { return it }
            .copy(confirmationRequests = confirmationRequests)
            .asSuccess()
    }
}
