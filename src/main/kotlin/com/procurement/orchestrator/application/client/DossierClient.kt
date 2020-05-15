package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

interface DossierClient {

    suspend fun validateRequirementResponse(
        id: CommandId,
        params: ValidateRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getSubmissionStateByIds(
        id: CommandId,
        params: GetSubmissionStateByIdsAction.Params
    ): Result<Reply<GetSubmissionStateByIdsAction.Result>, Fail.Incident>
}
