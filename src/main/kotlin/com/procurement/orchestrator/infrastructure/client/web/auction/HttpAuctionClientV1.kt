package com.procurement.orchestrator.infrastructure.client.web.auction

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AuctionClientV1
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClientV1
import com.procurement.orchestrator.infrastructure.client.web.auction.action.ScheduleAuctionsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpAuctionClientV1(private val webClient: WebClientV1, properties: ComponentProperties.Component) :
    AuctionClientV1 {

    private val url: URL = URL(properties.url + "/command")

    override suspend fun scheduleAuctions(
        id: CommandId,
        data: ScheduleAuctionsAction.Data,
        context: ScheduleAuctionsAction.Context
    ): Result<Reply<ScheduleAuctionsAction.ResponseData>, Fail.Incident> = webClient.call(
        url = url,
        command = AuctionCommandsV1.ScheduleAuctions.build(id, context, data),
        target = AuctionCommandsV1.ScheduleAuctions.target
    )
}
