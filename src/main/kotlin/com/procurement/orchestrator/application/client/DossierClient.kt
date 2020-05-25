package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

interface DossierClient {

    suspend fun getSubmissionPeriodEndDate(
        id: CommandId,
        params: GetSubmissionPeriodEndDateAction.Params
    ): Result<Reply<GetSubmissionPeriodEndDateAction.Result>, Fail.Incident>

    suspend fun validateRequirementResponse(
        id: CommandId,
        params: ValidateRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkAccessToSubmission(
        id: CommandId,
        params: CheckAccessToSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident>
}
