package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction

interface SubmissionClient {

    suspend fun doInvitations(
        id: CommandId,
        params: DoInvitationsAction.Params
    ): Result<Reply<DoInvitationsAction.Result>, Fail.Incident>

    suspend fun checkAbsenceActiveInvitations(
        id: CommandId,
        params: CheckAbsenceActiveInvitationsAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun setTenderPeriodAction(
        id: CommandId,
        params: SetTenderPeriodAction.Params
    ): Result<Reply<SetTenderPeriodAction.Result>, Fail.Incident>
}
