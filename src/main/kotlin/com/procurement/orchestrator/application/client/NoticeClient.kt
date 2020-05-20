package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.notice.action.CreateRecordAction
import com.procurement.orchestrator.infrastructure.client.web.notice.action.UpdateRecordAction

interface NoticeClient {

    suspend fun updateRecord(id: CommandId, params: UpdateRecordAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun createRecord(id: CommandId, params: CreateRecordAction.Params): Result<Reply<Unit>, Fail.Incident>
}
