package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

interface DossierClient {

    suspend fun validateRequirementResponse(params: ValidateRequirementResponseAction.Params): Result<Reply<Unit>, Fail.Incident>
}
