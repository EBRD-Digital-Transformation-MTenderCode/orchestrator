package com.procurement.orchestrator.infrastructure.client.web.revision

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RevisionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CheckAccessToAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.FindAmendmentIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetAmendmentByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.SetStateForAmendmentAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpRevisionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    RevisionClient {

    private val url: URL = URL(properties.url + "/command")

    override suspend fun checkAccessToAmendment(
        id: CommandId,
        params: CheckAccessToAmendmentAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RevisionCommands.CheckAccessToAmendment.build(id = id, params = params)
    )

    override suspend fun createAmendment(
        id: CommandId,
        params: CreateAmendmentAction.Params
    ): Result<Reply<CreateAmendmentAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RevisionCommands.CreateAmendment.build(id = id, params = params),
        target = RevisionCommands.CreateAmendment.target
    )

    override suspend fun dataValidation(
        id: CommandId,
        params: DataValidationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RevisionCommands.DataValidation.build(id = id, params = params)
    )

    override suspend fun findAmendmentIds(
        id: CommandId,
        params: FindAmendmentIdsAction.Params
    ): Result<Reply<FindAmendmentIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RevisionCommands.FindAmendmentIds.build(id = id, params = params),
        target = RevisionCommands.FindAmendmentIds.target
    )

    override suspend fun getMainPartOfAmendmentByIds(
        id: CommandId,
        params: GetAmendmentByIdsAction.Params
    ): Result<Reply<GetAmendmentByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RevisionCommands.GetAmendmentByIds.build(id = id, params = params),
        target = RevisionCommands.GetAmendmentByIds.target
    )

    override suspend fun setStateForAmendment(
        id: CommandId,
        params: SetStateForAmendmentAction.Params
    ): Result<Reply<SetStateForAmendmentAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RevisionCommands.SetStateForAmendment.build(id = id, params = params),
        target = RevisionCommands.SetStateForAmendment.target
    )
}
