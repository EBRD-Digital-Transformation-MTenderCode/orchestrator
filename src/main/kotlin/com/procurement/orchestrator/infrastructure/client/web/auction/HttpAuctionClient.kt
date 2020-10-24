package com.procurement.orchestrator.infrastructure.client.web.auction

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AuctionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.auction.action.ValidateAuctionsDataAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpAuctionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    AuctionClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun validateAuctionsData(
        id: CommandId,
        params: ValidateAuctionsDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AuctionCommands.ValidateAuctionsData.build(id = id, params = params)
    )
}
