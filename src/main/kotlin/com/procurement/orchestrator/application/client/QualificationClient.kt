package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
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

interface QualificationClient {

    suspend fun startQualificationPeriod(
        id: CommandId,
        params: StartQualificationPeriodAction.Params
    ): Result<Reply<StartQualificationPeriodAction.Result>, Fail.Incident>

    suspend fun findQualificationIds(
        id: CommandId,
        params: FindQualificationIdsAction.Params
    ): Result<Reply<FindQualificationIdsAction.Result>, Fail.Incident>

    suspend fun checkAccessToQualification(
        id: CommandId,
        params: CheckAccessToQualificationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkDeclaration(
        id: CommandId,
        params: CheckDeclarationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createQualification(
        id: CommandId,
        params: CreateQualificationAction.Params
    ): Result<Reply<CreateQualificationAction.Result>, Fail.Incident>

    suspend fun rankQualifications(
        id: CommandId,
        params: RankQualificationsAction.Params
    ): Result<Reply<RankQualificationsAction.Result>, Fail.Incident>

    suspend fun checkQualificationState(
        id: CommandId,
        params: CheckQualificationStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun doDeclaration(
        id: CommandId,
        params: DoDeclarationAction.Params
    ): Result<Reply<DoDeclarationAction.Result>, Fail.Incident>

    suspend fun findRequirementResponseByIds(
        id: CommandId,
        params: FindRequirementResponseByIdsAction.Params
    ): Result<Reply<FindRequirementResponseByIdsAction.Result>, Fail.Incident>

    suspend fun doConsideration(
        id: CommandId,
        params: DoConsiderationAction.Params
    ): Result<Reply<DoConsiderationAction.Result>, Fail.Incident>

    suspend fun doQualification(
        id: CommandId,
        params: DoQualificationAction.Params
    ): Result<Reply<DoQualificationAction.Result>, Fail.Incident>

    suspend fun setNextForQualificationAction(
        id: CommandId,
        params: SetNextForQualificationAction.Params
    ): Result<Reply<SetNextForQualificationAction.Result>, Fail.Incident>

    suspend fun analyzeQualificationForInvitation(
        id: CommandId,
        params: AnalyzeQualificationForInvitationAction.Params
    ): Result<Reply<AnalyzeQualificationForInvitationAction.Result>, Fail.Incident>

    suspend fun checkQualificationForProtocol(
        id: CommandId,
        params: CheckQualificationForProtocolAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun finalizeQualifications(
        id: CommandId,
        params: FinalizeQualificationsAction.Params
    ): Result<Reply<FinalizeQualificationsAction.Result>, Fail.Incident>

    suspend fun checkQualificationPeriod(
        id: CommandId,
        params: CheckQualificationPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun setQualificationPeriodEnd(
        id: CommandId,
        params: SetQualificationPeriodEndAction.Params
    ): Result<Reply<SetQualificationPeriodEndAction.Result>, Fail.Incident>
}
