package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckAccessToRequestOfConfirmationAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddGeneratedDocumentToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationRequestsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetOrganizationIdAndSourceOfRequestGroupAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.ValidateConfirmationResponseDataAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpContractingClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    ContractingClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun findCANIds(id: CommandId, params: FindCANIdsAction.Params):
        Result<Reply<FindCANIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.FindCANIds.build(id = id, params = params),
        target = ContractingCommands.FindCANIds.target
    )

    override suspend fun findSupplierReferencesOfActivePacs(
        id: CommandId,
        params: FindSupplierReferencesOfActivePacsAction.Params
    ):
        Result<Reply<FindSupplierReferencesOfActivePacsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.FindSupplierReferencesOfActivePacs.build(id = id, params = params),
        target = ContractingCommands.FindSupplierReferencesOfActivePacs.target
    )

    override suspend fun createFrameworkContract(id: CommandId, params: CreateFrameworkContractAction.Params):
        Result<Reply<CreateFrameworkContractAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CreateFrameworkContract.build(id = id, params = params),
        target = ContractingCommands.CreateFrameworkContract.target
    )

    override suspend fun cancelFrameworkContract(id: CommandId, params: CancelFrameworkContractAction.Params):
        Result<Reply<CancelFrameworkContractAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CancelFrameworkContract.build(id = id, params = params),
        target = ContractingCommands.CancelFrameworkContract.target
    )

    override suspend fun doPacs(
        id: CommandId,
        params: DoPacsAction.Params
    ): Result<Reply<DoPacsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.DoPacs.build(id = id, params = params),
        target = ContractingCommands.DoPacs.target
    )

    override suspend fun addSupplierReferencesInFC(
        id: CommandId,
        params: AddSupplierReferencesInFCAction.Params
    ): Result<Reply<AddSupplierReferencesInFCAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.AddSupplierReferencesInFC.build(id = id, params = params),
        target = ContractingCommands.AddSupplierReferencesInFC.target
    )

    override suspend fun addGeneratedDocumentToContract(
        id: CommandId,
        params: AddGeneratedDocumentToContractAction.Params
    ): Result<Reply<AddGeneratedDocumentToContractAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.AddGeneratedDocumentToContract.build(id = id, params = params),
        target = ContractingCommands.AddGeneratedDocumentToContract.target
    )

    override suspend fun setStateForContracts(
        id: CommandId,
        params: SetStateForContractsAction.Params
    ): Result<Reply<SetStateForContractsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.SetStateForContracts.build(id = id, params = params),
        target = ContractingCommands.SetStateForContracts.target
    )

    override suspend fun checkContractState(
        id: CommandId,
        params: CheckContractStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CheckContractState.build(id = id, params = params)
    )

    override suspend fun checkExistenceSupplierReferencesInFC(
        id: CommandId,
        params: CheckExistenceSupplierReferencesInFCAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CheckExistenceSupplierReferencesInFC.build(id = id, params = params)
    )

    override suspend fun getContractState(
        id: CommandId,
        params: GetContractStateAction.Params
    ): Result<Reply<GetContractStateAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.GetContractState.build(id = id, params = params),
        target = ContractingCommands.GetContractState.target
    )

    override suspend fun createConfirmationRequests(
        id: CommandId,
        params: CreateConfirmationRequestsAction.Params
    ): Result<Reply<CreateConfirmationRequestsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CreateConfirmationRequests.build(id = id, params = params),
        target = ContractingCommands.CreateConfirmationRequests.target
    )

    override suspend fun validateConfirmationResponseData(
        id: CommandId,
        params: ValidateConfirmationResponseDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.ValidateConfirmationResponseData.build(id = id, params = params)
    )

    override suspend fun getOrganizationIdAndSourceOfRequestGroup(
        id: CommandId,
        params: GetOrganizationIdAndSourceOfRequestGroupAction.Params
    ): Result<Reply<GetOrganizationIdAndSourceOfRequestGroupAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.GetOrganizationIdAndSourceOfRequestGroup.build(id = id, params = params),
        target = ContractingCommands.GetOrganizationIdAndSourceOfRequestGroup.target
    )

    override suspend fun checkAccessToRequestOfConfirmation(
            id: CommandId,
            params: CheckAccessToRequestOfConfirmationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
                url = url,
                command = ContractingCommands.CheckAccessToRequestOfConfirmation.build(id = id, params = params)
        )
}
