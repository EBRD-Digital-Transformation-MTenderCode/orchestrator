package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetAmendmentIdsAction

interface RevisionClient {

    suspend fun getAmendmentIds(params: GetAmendmentIdsAction.Params): Result<Reply<GetAmendmentIdsAction.Result>, Fail.Incident>

    suspend fun createAmendment(params: CreateAmendmentAction.Params): Result<Reply<CreateAmendmentAction.Result>, Fail.Incident>

    suspend fun dataValidation(params: DataValidationAction.Params): Result<Reply<Unit>, Fail.Incident>
}
