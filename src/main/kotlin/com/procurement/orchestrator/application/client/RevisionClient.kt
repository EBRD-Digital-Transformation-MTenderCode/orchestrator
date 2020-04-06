package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CheckAccessToAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetAmendmentIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetMainPartOfAmendmentByIdsAction

interface RevisionClient {

    suspend fun checkAccessToAmendment(
        id: CommandId,
        params: CheckAccessToAmendmentAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createAmendment(
        id: CommandId,
        params: CreateAmendmentAction.Params
    ): Result<Reply<CreateAmendmentAction.Result>, Fail.Incident>

    suspend fun dataValidation(id: CommandId, params: DataValidationAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun getAmendmentIds(
        id: CommandId,
        params: GetAmendmentIdsAction.Params
    ): Result<Reply<GetAmendmentIdsAction.Result>, Fail.Incident>

    suspend fun getMainPartOfAmendmentByIds(
        id: CommandId,
        params: GetMainPartOfAmendmentByIdsAction.Params
    ): Result<Reply<GetMainPartOfAmendmentByIdsAction.Result>, Fail.Incident>
}
