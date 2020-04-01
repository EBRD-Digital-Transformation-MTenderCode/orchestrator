package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.application.client.EvaluateClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpEvaluateClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    EvaluateClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun checkAccessToAward(params: CheckAccessToAwardAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluateCommands.CheckAccessToAward.build(params = params)
        )
}
