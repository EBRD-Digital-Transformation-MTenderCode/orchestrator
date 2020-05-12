package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.GetSubmissionStateByIdsAction

interface QualificationClient {

    suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getSubmissionStateByIds(
        id: CommandId,
        params: GetSubmissionStateByIdsAction.Params
    ): Result<Reply<GetSubmissionStateByIdsAction.Result>, Fail.Incident>
}
