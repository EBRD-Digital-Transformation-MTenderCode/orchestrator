package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpContractingClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    ContractingClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun findCANIds(id: CommandId, params: FindCANIdsAction.Params):
        Result<Reply<FindCANIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.FindCANIds.build(id = id, params = params),
        target = ContractingCommands.FindCANIds.target
    )

    override suspend fun createFrameworkContract(id: CommandId, params: CreateFrameworkContractAction.Params):
        Result<Reply<CreateFrameworkContractAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CreateFrameworkContract.build(id = id, params = params),
        target = ContractingCommands.CreateFrameworkContract.target
    )

    override suspend fun cancelFrameworkContract(id: CommandId, params: CancelFrameworkContractAction.Params):
        Result<Reply<CancelFrameworkContractAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = ContractingCommands.CancelFrameworkContract.build(id = id, params = params),
        target = ContractingCommands.CancelFrameworkContract.target
    )

}
