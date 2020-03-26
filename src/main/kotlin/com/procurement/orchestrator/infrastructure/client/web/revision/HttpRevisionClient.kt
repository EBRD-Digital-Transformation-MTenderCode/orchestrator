package com.procurement.orchestrator.infrastructure.client.web.revision

import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetAmendmentIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpRevisionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    RevisionClient {

    private val url: URL = URL(properties.url + "/command")

    override suspend fun getAmendmentIds(params: GetAmendmentIdsAction.Params): Result<Reply<GetAmendmentIdsAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = RevisionCommands.GetAmendmentIds.build(params = params),
            target = RevisionCommands.GetAmendmentIds.target
        )

    override suspend fun createAmendment(params: CreateAmendmentAction.Params): Result<Reply<CreateAmendmentAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = RevisionCommands.CreateAmendment.build(params = params),
            target = RevisionCommands.CreateAmendment.target
        )

    override suspend fun dataValidation(params: DataValidationAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = RevisionCommands.DataValidation.build(params = params)
        )
}
