package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.storage.action.CheckRegistrationAction
import com.procurement.orchestrator.infrastructure.client.web.storage.action.OpenAccessAction

interface StorageClient {

    suspend fun checkRegistration(params: CheckRegistrationAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun openAccess(params: OpenAccessAction.Params): Result<Reply<OpenAccessAction.Result>, Fail.Incident>
}
