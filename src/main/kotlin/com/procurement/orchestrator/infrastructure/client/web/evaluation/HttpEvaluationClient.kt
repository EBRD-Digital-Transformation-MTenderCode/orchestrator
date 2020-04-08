package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateUnsuccessfulAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpEvaluationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    EvaluationClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun checkAccessToAward(
        id: CommandId,
        params: CheckAccessToAwardAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CheckAccessToAward.build(id = id, params = params)
    )

    override suspend fun checkRelatedTenderer(
        id: CommandId,
        params: CheckRelatedTendererAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CheckRelatedTenderer.build(id = id, params = params)
    )

    override suspend fun createRequirementResponse(
        id: CommandId,
        params: CreateRequirementResponseAction.Params
    ): Result<Reply<CreateRequirementResponseAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CreateRequirementResponse.build(id = id, params = params),
        target = EvaluationCommands.CreateRequirementResponse.target
    )

    override suspend fun getAwardStateByIds(
        id: CommandId,
        params: GetAwardStateByIdsAction.Params
    ): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.GetAwardStateByIds.build(id = id, params = params),
        target = EvaluationCommands.GetAwardStateByIds.target
    )

    override suspend fun createUnsuccessfulAwards(
        id: CommandId,
        params: CreateUnsuccessfulAwardsAction.Params
    ): Result<Reply<CreateUnsuccessfulAwardsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CreateUnsuccessfulAwards.build(id = id, params = params),
        target = EvaluationCommands.CreateUnsuccessfulAwards.target
    )
}
