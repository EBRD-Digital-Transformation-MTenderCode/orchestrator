package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddGeneratedDocumentToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction

interface ContractingClient {

    suspend fun findCANIds(
        id: CommandId,
        params: FindCANIdsAction.Params
    ): Result<Reply<FindCANIdsAction.Result>, Fail.Incident>

    suspend fun findSupplierReferencesOfActivePacs(
        id: CommandId,
        params: FindSupplierReferencesOfActivePacsAction.Params
    ): Result<Reply<FindSupplierReferencesOfActivePacsAction.Result>, Fail.Incident>

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
}
