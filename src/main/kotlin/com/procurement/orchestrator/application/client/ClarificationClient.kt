package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiriesAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiryIdsAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.GetEnquiryByIdsAction

interface ClarificationClient {

    suspend fun findEnquiryIds(
        id: CommandId,
        params: FindEnquiryIdsAction.Params
    ): Result<Reply<FindEnquiryIdsAction.Result>, Fail.Incident>

    suspend fun getEnquiryByIds(
        id: CommandId,
        params: GetEnquiryByIdsAction.Params
    ): Result<Reply<GetEnquiryByIdsAction.Result>, Fail.Incident>

    suspend fun findEnquiries(
        id: CommandId,
        params: FindEnquiriesAction.Params
    ): Result<Reply<FindEnquiriesAction.Result>, Fail.Incident>

}
