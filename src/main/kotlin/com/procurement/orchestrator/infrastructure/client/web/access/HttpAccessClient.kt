package com.procurement.orchestrator.infrastructure.client.web.access

import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonsStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpAccessClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    AccessClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun getLotIds(params: GetLotIdsAction.Params): Result<Reply<GetLotIdsAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = AccessCommands.GetLotByIds.build(params = params),
            target = AccessCommands.GetLotByIds.target
        )

    override suspend fun getLotStateByIds(params: GetLotStateByIdsAction.Params): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident> =
        webClient.call(
            url = url,
            command = AccessCommands.GetLotStateByIds.build(params = params),
            target = AccessCommands.GetLotStateByIds.target
        )

    override suspend fun checkAccessToTender(params: CheckAccessToTenderAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = AccessCommands.CheckAccessToTender.build(params = params)
        )

    override suspend fun checkPersonsStructure(params: CheckPersonsStructureAction.Params): Result<Reply<Unit>, Fail.Incident> =
        webClient.call(
            url = url,
            command = AccessCommands.CheckPersonsStructure.build(params = params)
        )
}
