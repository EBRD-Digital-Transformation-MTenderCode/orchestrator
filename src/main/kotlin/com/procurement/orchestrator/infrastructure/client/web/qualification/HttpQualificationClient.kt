package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpQualificationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    QualificationClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun createSubmission(
        id: CommandId,
        params: CreateSubmissionAction.Params
    ): Result<Reply<CreateSubmissionAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CreateSubmission.build(id = id, params = params),
        target = QualificationCommands.CreateSubmission.target
    )

    override suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckPeriod.build(id = id, params = params)
    )

    override suspend fun validateSubmission(
        id: CommandId,
        params: ValidateSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.ValidateSubmission.build(id = id, params = params)
    )
}
