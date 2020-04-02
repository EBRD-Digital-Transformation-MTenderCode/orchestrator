package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.application.client.EvaluationClient
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

class HttpEvaluationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    EvaluationClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun checkAccessToAward(params: CheckAccessToAwardAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluationCommands.CheckAccessToAward.build(params = params)
        )

    override suspend fun checkRelatedTenderer(params: CheckRelatedTendererAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluationCommands.CheckRelatedTenderer.build(params = params)
        )

    override suspend fun createRequirementResponse(params: CreateRequirementResponseAction.Params): Result<Reply<CreateRequirementResponseAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluationCommands.CreateRequirementResponse.build(params = params),
            target = EvaluationCommands.CreateRequirementResponse.target
        )

    override suspend fun getAwardStateByIds(params: GetAwardStateByIdsAction.Params): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = EvaluationCommands.GetAwardStateByIds.build(params = params),
            target = EvaluationCommands.GetAwardStateByIds.target
        )
}
