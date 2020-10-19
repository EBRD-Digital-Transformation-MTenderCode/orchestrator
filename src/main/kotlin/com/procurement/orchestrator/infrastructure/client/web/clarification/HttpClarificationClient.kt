package com.procurement.orchestrator.infrastructure.client.web.clarification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ClarificationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.CreateEnquiryPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiriesAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiryIdsAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.GetEnquiryByIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpClarificationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    ClarificationClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun findEnquiryIds(
        id: CommandId,
        params: FindEnquiryIdsAction.Params
    ): Result<Reply<FindEnquiryIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ClarificationCommands.FindEnquiryIds.build(id = id, params = params),
        target = ClarificationCommands.FindEnquiryIds.target
    )

    override suspend fun getEnquiryByIds(
        id: CommandId,
        params: GetEnquiryByIdsAction.Params
    ): Result<Reply<GetEnquiryByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ClarificationCommands.GetEnquiryByIds.build(id = id, params = params),
        target = ClarificationCommands.GetEnquiryByIds.target
    )

    override suspend fun findEnquiries(
        id: CommandId,
        params: FindEnquiriesAction.Params
    ): Result<Reply<FindEnquiriesAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ClarificationCommands.FindEnquiries.build(id = id, params = params),
        target = ClarificationCommands.FindEnquiries.target
    )

    override suspend fun createEnquiryPeriod(
        id: CommandId,
        params: CreateEnquiryPeriodAction.Params
    ): Result<Reply<CreateEnquiryPeriodAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ClarificationCommands.CreateEnquiryPeriod.build(id = id, params = params),
        target = ClarificationCommands.CreateEnquiryPeriod.target
    )
}
