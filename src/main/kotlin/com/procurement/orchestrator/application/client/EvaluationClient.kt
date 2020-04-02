package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction

interface EvaluationClient {

    suspend fun checkAccessToAward(params: CheckAccessToAwardAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkRelatedTenderer(params: CheckRelatedTendererAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun createRequirementResponse(params: CreateRequirementResponseAction.Params): Result<Reply<CreateRequirementResponseAction.Result>, Fail.Incident>

    suspend fun getAwardStateByIds(params: GetAwardStateByIdsAction.Params): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident>
}
