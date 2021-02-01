package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction

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
}
