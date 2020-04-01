package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction

interface EvaluateClient {

    suspend fun checkAccessToAward(params: CheckAccessToAwardAction.Params): Result<Reply<Unit>, Fail.Incident>
}
