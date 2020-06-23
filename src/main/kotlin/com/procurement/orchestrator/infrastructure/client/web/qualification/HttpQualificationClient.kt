package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DetermineNextsForQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.StartQualificationPeriodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpQualificationClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    QualificationClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun startQualificationPeriod(
        id: CommandId,
        params: StartQualificationPeriodAction.Params
    ): Result<Reply<StartQualificationPeriodAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.StartQualificationPeriod.build(id = id, params = params),
        target = QualificationCommands.StartQualificationPeriod.target
    )

    override suspend fun findQualificationIds(
        id: CommandId,
        params: FindQualificationIdsAction.Params
    ): Result<Reply<FindQualificationIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.FindQualificationIds.build(id = id, params = params),
        target = QualificationCommands.FindQualificationIds.target
    )

    override suspend fun checkDeclaration(
        id: CommandId,
        params: CheckDeclarationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckDeclaration.build(id = id, params = params)
    )

    override suspend fun createQualification(
        id: CommandId,
        params: CreateQualificationAction.Params
    ): Result<Reply<CreateQualificationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CreateQualification.build(id = id, params = params),
        target = QualificationCommands.CreateQualification.target
    )

    override suspend fun determineNextsForQualification(
        id: CommandId,
        params: DetermineNextsForQualificationAction.Params
    ): Result<Reply<DetermineNextsForQualificationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.DetermineNextsForQualification.build(id = id, params = params),
        target = QualificationCommands.DetermineNextsForQualification.target
    )
}
