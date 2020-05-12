package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpQualificationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    QualificationClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckPeriod.build(id = id, params = params)
    )

    override suspend fun checkAccessToSubmission(
        id: CommandId,
        params: CheckAccessToSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckAccessToSubmission.build(id = id, params = params)
    )
}
