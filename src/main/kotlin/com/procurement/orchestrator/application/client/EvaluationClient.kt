package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
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

interface EvaluationClient {

    suspend fun checkAccessToAward(
        id: CommandId,
        params: CheckAccessToAwardAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkRelatedTenderer(
        id: CommandId,
        params: CheckRelatedTendererAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun addRequirementResponse(
        id: CommandId,
        params: AddRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getAwardStateByIds(
        id: CommandId,
        params: GetAwardStateByIdsAction.Params
    ): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident>

    suspend fun createUnsuccessfulAwards(
        id: CommandId,
        params: CreateUnsuccessfulAwardsAction.Params
    ): Result<Reply<CreateUnsuccessfulAwardsAction.Result>, Fail.Incident>

    suspend fun closeAwardPeriod(
        id: CommandId,
        params: CloseAwardPeriodAction.Params
    ): Result<Reply<CloseAwardPeriodAction.Result>, Fail.Incident>

    suspend fun startAwardPeriod(
        id: CommandId,
        params: StartAwardPeriodAction.Params
    ): Result<Reply<StartAwardPeriodAction.Result>, Fail.Incident>

    suspend fun validateAwardData(
        id: CommandId,
        params: ValidateAwardDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createAward(
        id: CommandId,
        params: CreateAwardAction.Params
    ): Result<Reply<CreateAwardAction.Result>, Fail.Incident>

    suspend fun checkAwardsState(
        id: CommandId,
        params: CheckAwardsStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getAwardByIds(
        id: CommandId,
        params: GetAwardByIdsAction.Params
    ): Result<Reply<GetAwardByIdsAction.Result>, Fail.Incident>

    suspend fun updateAward(
        id: CommandId,
        params: UpdateAwardAction.Params
    ): Result<Reply<UpdateAwardAction.Result>, Fail.Incident>

    suspend fun findAwardsForProtocol(
        id: CommandId,
        params: FindAwardsForProtocolAction.Params
    ): Result<Reply<FindAwardsForProtocolAction.Result>, Fail.Incident>

    suspend fun doConsideration(
        id: CommandId,
        params: DoConsiderationAction.Params
    ): Result<Reply<DoConsiderationAction.Result>, Fail.Incident>

    suspend fun finalizeAwards(
        id: CommandId,
        params: FinalizeAwardsAction.Params
    ): Result<Reply<FinalizeAwardsAction.Result>, Fail.Incident>

    suspend fun getRelatedAwardByIds(
        id: CommandId,
        params: GetRelatedAwardByIdsAction.Params
    ): Result<Reply<GetRelatedAwardByIdsAction.Result>, Fail.Incident>
}
