package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpSubmissionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    SubmissionClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun doInvitations(
        id: CommandId,
        params: DoInvitationsAction.Params
    ): Result<Reply<DoInvitationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.DoInvitations.build(id = id, params = params),
        target = SubmissionCommands.DoInvitations.target
    )

    override suspend fun checkAbsenceActiveInvitations(
        id: CommandId,
        params: CheckAbsenceActiveInvitationsAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CheckAbsenceActiveInvitations.build(id = id, params = params)
    )

    override suspend fun validateTenderPeriod(
        id: CommandId,
        params: ValidateTenderPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.ValidateTenderPeriod.build(id = id, params = params)
    )
}
