package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiryIdsAction

interface ClarificationClient {

    suspend fun findEnquiryIds(
        id: CommandId,
        params: FindEnquiryIdsAction.Params
    ): Result<Reply<FindEnquiryIdsAction.Result>, Fail.Incident>
}
