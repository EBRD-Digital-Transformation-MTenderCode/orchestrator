package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.auction.action.ScheduleAuctionsAction

interface AuctionClientV1 {

    suspend fun scheduleAuctions(
        id: CommandId,
        data: ScheduleAuctionsAction.Data,
        context: ScheduleAuctionsAction.Context
    ): Result<Reply<ScheduleAuctionsAction.ResponseData>, Fail.Incident>
}
