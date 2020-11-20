package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction

interface ContractingClient {

    suspend fun findCANIds(
        id: CommandId,
        params: FindCANIdsAction.Params
    ): Result<Reply<FindCANIdsAction.Result>, Fail.Incident>

    suspend fun createContract(
        id: CommandId,
        params: CreateContractAction.Params
    ): Result<Reply<CreateContractAction.Result>, Fail.Incident>

}
