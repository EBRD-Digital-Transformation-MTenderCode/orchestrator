package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.auction.action.ValidateAuctionsDataAction

interface AuctionClient {

    suspend fun validateAuctionsData(
        id: CommandId,
        params: ValidateAuctionsDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident>
}
