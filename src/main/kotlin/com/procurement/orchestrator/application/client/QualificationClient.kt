package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateSubmissionAction

interface QualificationClient {

    suspend fun createSubmission(
        id: CommandId,
        params: CreateSubmissionAction.Params
    ): Result<Reply<CreateSubmissionAction.Result>, Fail.Incident>

    suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun validateSubmission(
        id: CommandId,
        params: ValidateSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

}
