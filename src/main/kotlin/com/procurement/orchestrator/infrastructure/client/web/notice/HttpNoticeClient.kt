package com.procurement.orchestrator.infrastructure.client.web.notice

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.NoticeClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.notice.action.CreateRecordAction
import com.procurement.orchestrator.infrastructure.client.web.notice.action.UpdateRecordAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpNoticeClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    NoticeClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun updateRecord(
        id: CommandId,
        params: UpdateRecordAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = NoticeCommands.UpdateRecord.build(id = id, params = params)
    )

    override suspend fun createRecord(
        id: CommandId,
        params: CreateRecordAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = NoticeCommands.CreateRecord.build(id = id, params = params)
    )
}
