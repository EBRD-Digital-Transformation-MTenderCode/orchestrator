package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction

interface ContractingClient {

    suspend fun findCANIds(
        id: CommandId,
        params: FindCANIdsAction.Params
    ): Result<Reply<FindCANIdsAction.Result>, Fail.Incident>

    suspend fun createFrameworkContract(
        id: CommandId,
        params: CreateFrameworkContractAction.Params
    ): Result<Reply<CreateFrameworkContractAction.Result>, Fail.Incident>

    suspend fun cancelFrameworkContract(
        id: CommandId,
        params: CancelFrameworkContractAction.Params
    ): Result<Reply<CancelFrameworkContractAction.Result>, Fail.Incident>

}
