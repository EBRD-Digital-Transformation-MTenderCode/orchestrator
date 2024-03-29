package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.AddRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAwardsStateAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CloseAwardPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateUnsuccessfulAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.DoConsiderationAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.FinalizeAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.FindAwardsForProtocolAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetRelatedAwardByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.StartAwardPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.UpdateAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.ValidateAwardDataAction
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

    override suspend fun addRequirementResponse(
        id: CommandId,
        params: AddRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.AddRequirementResponse.build(id = id, params = params)
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

    override suspend fun closeAwardPeriod(
        id: CommandId,
        params: CloseAwardPeriodAction.Params
    ): Result<Reply<CloseAwardPeriodAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CloseAwardPeriod.build(id = id, params = params),
        target = EvaluationCommands.CloseAwardPeriod.target
    )

    override suspend fun startAwardPeriod(
        id: CommandId,
        params: StartAwardPeriodAction.Params
    ): Result<Reply<StartAwardPeriodAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.StartAwardPeriod.build(id = id, params = params),
        target = EvaluationCommands.StartAwardPeriod.target
    )

    override suspend fun validateAwardData(
        id: CommandId,
        params: ValidateAwardDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.ValidateAwardData.build(id = id, params = params)
    )

    override suspend fun createAward(
        id: CommandId,
        params: CreateAwardAction.Params
    ): Result<Reply<CreateAwardAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CreateAward.build(id = id, params = params),
        target = EvaluationCommands.CreateAward.target
    )

    override suspend fun checkAwardsState(
        id: CommandId,
        params: CheckAwardsStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.CheckAwardsState.build(id = id, params = params)
    )

    override suspend fun getAwardByIds(
        id: CommandId,
        params: GetAwardByIdsAction.Params
    ): Result<Reply<GetAwardByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.GetAwardByIds.build(id = id, params = params),
        target = EvaluationCommands.GetAwardByIds.target
    )

    override suspend fun updateAward(
        id: CommandId,
        params: UpdateAwardAction.Params
    ): Result<Reply<UpdateAwardAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.UpdateAward.build(id = id, params = params),
        target = EvaluationCommands.UpdateAward.target
    )

    override suspend fun findAwardsForProtocol(
        id: CommandId,
        params: FindAwardsForProtocolAction.Params
    ): Result<Reply<FindAwardsForProtocolAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.FindAwardsForProtocol.build(id = id, params = params),
        target = EvaluationCommands.FindAwardsForProtocol.target
    )

    override suspend fun doConsideration(
        id: CommandId,
        params: DoConsiderationAction.Params
    ): Result<Reply<DoConsiderationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.DoConsideration.build(id = id, params = params),
        target = EvaluationCommands.DoConsideration.target
    )

    override suspend fun finalizeAwards(
        id: CommandId,
        params: FinalizeAwardsAction.Params
    ): Result<Reply<FinalizeAwardsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.FinalizeAwards.build(id = id, params = params),
        target = EvaluationCommands.FinalizeAwards.target
    )

    override suspend fun getRelatedAwardByIds(
        id: CommandId,
        params: GetRelatedAwardByIdsAction.Params
    ): Result<Reply<GetRelatedAwardByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = EvaluationCommands.GetRelatedAwardByIds.build(id = id, params = params),
        target = EvaluationCommands.GetRelatedAwardByIds.target
    )
}
