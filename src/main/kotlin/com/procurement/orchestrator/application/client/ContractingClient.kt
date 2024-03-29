package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddGeneratedDocumentToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckAccessToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckAccessToRequestOfConfirmationAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceOfConfirmationResponsesAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckRelatedContractsStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationRequestsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationResponseAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindContractDocumentIdAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetAwardIdByPacAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetOrganizationIdAndSourceOfRequestGroupAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetRelatedAwardIdByCansAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetSupplierIdsByContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.ValidateConfirmationResponseDataAction

interface ContractingClient {

    suspend fun findCANIds(
        id: CommandId,
        params: FindCANIdsAction.Params
    ): Result<Reply<FindCANIdsAction.Result>, Fail.Incident>

    suspend fun findSupplierReferencesOfActivePacs(
        id: CommandId,
        params: FindSupplierReferencesOfActivePacsAction.Params
    ): Result<Reply<FindSupplierReferencesOfActivePacsAction.Result>, Fail.Incident>

    suspend fun findContractDocumentId(
        id: CommandId,
        params: FindContractDocumentIdAction.Params
    ): Result<Reply<FindContractDocumentIdAction.Result>, Fail.Incident>

    suspend fun createFrameworkContract(
        id: CommandId,
        params: CreateFrameworkContractAction.Params
    ): Result<Reply<CreateFrameworkContractAction.Result>, Fail.Incident>

    suspend fun cancelFrameworkContract(
        id: CommandId,
        params: CancelFrameworkContractAction.Params
    ): Result<Reply<CancelFrameworkContractAction.Result>, Fail.Incident>

    suspend fun doPacs(
        id: CommandId,
        params: DoPacsAction.Params
    ): Result<Reply<DoPacsAction.Result>, Fail.Incident>

    suspend fun addSupplierReferencesInFC(
        id: CommandId,
        params: AddSupplierReferencesInFCAction.Params
    ): Result<Reply<AddSupplierReferencesInFCAction.Result>, Fail.Incident>

    suspend fun addGeneratedDocumentToContract(
        id: CommandId,
        params: AddGeneratedDocumentToContractAction.Params
    ): Result<Reply<AddGeneratedDocumentToContractAction.Result>, Fail.Incident>

    suspend fun setStateForContracts(
        id: CommandId,
        params: SetStateForContractsAction.Params
    ): Result<Reply<SetStateForContractsAction.Result>, Fail.Incident>

    suspend fun checkContractState(
        id: CommandId,
        params: CheckContractStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkExistenceSupplierReferencesInFC(
        id: CommandId,
        params: CheckExistenceSupplierReferencesInFCAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getContractState(
        id: CommandId,
        params: GetContractStateAction.Params
    ): Result<Reply<GetContractStateAction.Result>, Fail.Incident>

    suspend fun getAwardIdByPac(
        id: CommandId,
        params: GetAwardIdByPacAction.Params
    ): Result<Reply<GetAwardIdByPacAction.Result>, Fail.Incident>

    suspend fun createConfirmationRequests(
        id: CommandId,
        params: CreateConfirmationRequestsAction.Params
    ): Result<Reply<CreateConfirmationRequestsAction.Result>, Fail.Incident>

    suspend fun validateConfirmationResponseData(
        id: CommandId,
        params: ValidateConfirmationResponseDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getOrganizationIdAndSourceOfRequestGroup(
        id: CommandId,
        params: GetOrganizationIdAndSourceOfRequestGroupAction.Params
    ): Result<Reply<GetOrganizationIdAndSourceOfRequestGroupAction.Result>, Fail.Incident>

    suspend fun checkAccessToRequestOfConfirmation(
            id: CommandId,
            params: CheckAccessToRequestOfConfirmationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createConfirmationResponse(
        id: CommandId,
        params: CreateConfirmationResponseAction.Params
    ): Result<Reply<CreateConfirmationResponseAction.Result>, Fail.Incident>

    suspend fun checkAccessToContract(
        id: CommandId,
        params: CheckAccessToContractAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkExistenceOfConfirmationResponses(
        id: CommandId,
        params: CheckExistenceOfConfirmationResponsesAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getSuppliersIdsByContract(
        id: CommandId,
        params: GetSupplierIdsByContractAction.Params
    ): Result<Reply<GetSupplierIdsByContractAction.Result>, Fail.Incident>

    suspend fun checkRelatedContractsState(
        id: CommandId,
        params: CheckRelatedContractsStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getRelatedAwardIdByCans(
        id: CommandId,
        params: GetRelatedAwardIdByCansAction.Params
    ): Result<Reply<GetRelatedAwardIdByCansAction.Result>, Fail.Incident>

    suspend fun createContract(
        id: CommandId,
        params: CreateContractAction.Params
    ): Result<Reply<CreateContractAction.Result>, Fail.Incident>
}
