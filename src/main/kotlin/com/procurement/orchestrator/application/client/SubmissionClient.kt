package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateBidDataAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction

interface SubmissionClient {

    suspend fun doInvitations(
        id: CommandId,
        params: DoInvitationsAction.Params
    ): Result<Reply<DoInvitationsAction.Result>, Fail.Incident>

    suspend fun publishInvitations(
        id: CommandId,
        params: PublishInvitationsAction.Params
    ): Result<Reply<PublishInvitationsAction.Result>, Fail.Incident>

    suspend fun checkAbsenceActiveInvitations(
        id: CommandId,
        params: CheckAbsenceActiveInvitationsAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun validateTenderPeriod(
        id: CommandId,
        params: ValidateTenderPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun setTenderPeriodAction(
        id: CommandId,
        params: SetTenderPeriodAction.Params
    ): Result<Reply<SetTenderPeriodAction.Result>, Fail.Incident>

    suspend fun checkPeriod(id: CommandId, params: CheckPeriodAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun validateBidData(id: CommandId, params: ValidateBidDataAction.Params): Result<Reply<Unit>, Fail.Incident>
}
