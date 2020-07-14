package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpSubmissionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    SubmissionClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun checkAbsenceActiveInvitations(
        id: CommandId,
        params: CheckAbsenceActiveInvitationsAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CheckAbsenceActiveInvitations.build(id = id, params = params)
    )
}
