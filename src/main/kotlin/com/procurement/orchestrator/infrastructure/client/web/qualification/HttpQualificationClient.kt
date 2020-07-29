package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.AnalyzeQualificationForInvitationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckAccessToQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckQualificationForProtocolAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckQualificationPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckQualificationStateAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoConsiderationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FinalizeQualificationsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindRequirementResponseByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.RankQualificationsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.SetNextForQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.SetQualificationPeriodEndAction
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

    override suspend fun checkAccessToQualification(
        id: CommandId,
        params: CheckAccessToQualificationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckAccessToQualification.build(id = id, params = params)
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

    override suspend fun rankQualifications(
        id: CommandId,
        params: RankQualificationsAction.Params
    ): Result<Reply<RankQualificationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.RankQualifications.build(id = id, params = params),
        target = QualificationCommands.RankQualifications.target
    )

    override suspend fun checkQualificationState(
        id: CommandId,
        params: CheckQualificationStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckQualificationState.build(id = id, params = params)
    )

    override suspend fun doDeclaration(
        id: CommandId,
        params: DoDeclarationAction.Params
    ): Result<Reply<DoDeclarationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.DoDeclaration.build(id = id, params = params),
        target = QualificationCommands.DoDeclaration.target
    )

    override suspend fun findRequirementResponseByIds(
        id: CommandId,
        params: FindRequirementResponseByIdsAction.Params
    ): Result<Reply<FindRequirementResponseByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.FindRequirementResponseByIds.build(id = id, params = params),
        target = QualificationCommands.FindRequirementResponseByIds.target
    )

    override suspend fun doConsideration(
        id: CommandId,
        params: DoConsiderationAction.Params
    ): Result<Reply<DoConsiderationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.DoConsideration.build(id = id, params = params),
        target = QualificationCommands.DoConsideration.target
    )

    override suspend fun doQualification(
        id: CommandId,
        params: DoQualificationAction.Params
    ): Result<Reply<DoQualificationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.DoQualification.build(id = id, params = params),
        target = QualificationCommands.DoQualification.target
    )

    override suspend fun setNextForQualificationAction(
        id: CommandId,
        params: SetNextForQualificationAction.Params
    ): Result<Reply<SetNextForQualificationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.SetNextForQualification.build(id = id, params = params),
        target = QualificationCommands.SetNextForQualification.target
    )

    override suspend fun analyzeQualificationForInvitation(
        id: CommandId,
        params: AnalyzeQualificationForInvitationAction.Params
    ): Result<Reply<AnalyzeQualificationForInvitationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.AnalyzeQualificationForInvitation.build(id = id, params = params),
        target = QualificationCommands.AnalyzeQualificationForInvitation.target
    )

    override suspend fun checkQualificationForProtocol(
        id: CommandId,
        params: CheckQualificationForProtocolAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckQualificationForProtocol.build(id = id, params = params)
    )

    override suspend fun finalizeQualifications(
        id: CommandId,
        params: FinalizeQualificationsAction.Params
    ): Result<Reply<FinalizeQualificationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.FinalizeQualifications.build(id = id, params = params),
        target = QualificationCommands.FinalizeQualifications.target
    )

    override suspend fun checkQualificationPeriod(
        id: CommandId,
        params: CheckQualificationPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.CheckQualificationPeriod.build(id = id, params = params)
    )

    override suspend fun setQualificationPeriodEnd(
        id: CommandId,
        params: SetQualificationPeriodEndAction.Params
    ): Result<Reply<SetQualificationPeriodEndAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = QualificationCommands.SetQualificationPeriodEnd.build(id = id, params = params),
        target = QualificationCommands.SetQualificationPeriodEnd.target
    )
}
