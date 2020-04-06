package com.procurement.orchestrator.infrastructure.client.web.storage

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.StorageClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.storage.action.CheckRegistrationAction
import com.procurement.orchestrator.infrastructure.client.web.storage.action.OpenAccessAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpStorageClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    StorageClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun checkRegistration(
        id: CommandId,
        params: CheckRegistrationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = StorageCommands.CheckRegistration.build(id = id, params = params)
    )

    override suspend fun openAccess(
        id: CommandId,
        params: OpenAccessAction.Params
    ): Result<Reply<OpenAccessAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = StorageCommands.OpenAccess.build(id = id, params = params),
        target = StorageCommands.OpenAccess.target
    )
}
