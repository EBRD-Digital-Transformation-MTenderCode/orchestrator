package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.application.client.EvaluateClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
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

    override suspend fun checkRelatedTenderer(params: CheckRelatedTendererAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluateCommands.CheckRelatedTenderer.build(params = params)
        )

    override suspend fun createRequirementResponse(params: CreateRequirementResponseAction.Params): Result<Reply<CreateRequirementResponseAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluateCommands.CreateRequirementResponse.build(params = params),
            target = EvaluateCommands.CreateRequirementResponse.target
        )

    override suspend fun getAwardStateByIds(params: GetAwardStateByIdsAction.Params): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluateCommands.GetAwardStateByIds.build(params = params),
            target = EvaluateCommands.GetAwardStateByIds.target
        )
}
