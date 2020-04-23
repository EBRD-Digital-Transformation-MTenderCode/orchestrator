package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CheckAccessToAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.FindAmendmentIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetAmendmentByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.SetStateForAmendmentAction

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
    suspend fun findAmendmentIds(
        id: CommandId,
        params: FindAmendmentIdsAction.Params
    ): Result<Reply<FindAmendmentIdsAction.Result>, Fail.Incident>

    suspend fun getAmendmentByIds(
        id: CommandId,
        params: GetAmendmentByIdsAction.Params
    ): Result<Reply<GetAmendmentByIdsAction.Result>, Fail.Incident>

    suspend fun setStateForAmendment(
        id: CommandId,
        params: SetStateForAmendmentAction.Params
    ): Result<Reply<SetStateForAmendmentAction.Result>, Fail.Incident>
}
