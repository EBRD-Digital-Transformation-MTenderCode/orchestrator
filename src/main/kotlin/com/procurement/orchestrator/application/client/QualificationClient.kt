package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DetermineNextsForQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindRequirementResponseByIdsAction
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

    suspend fun checkDeclaration(
        id: CommandId,
        params: CheckDeclarationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createQualification(
        id: CommandId,
        params: CreateQualificationAction.Params
    ): Result<Reply<CreateQualificationAction.Result>, Fail.Incident>

    suspend fun determineNextsForQualification(
        id: CommandId,
        params: DetermineNextsForQualificationAction.Params
    ): Result<Reply<DetermineNextsForQualificationAction.Result>, Fail.Incident>

    suspend fun doDeclaration(
        id: CommandId,
        params: DoDeclarationAction.Params
    ): Result<Reply<DoDeclarationAction.Result>, Fail.Incident>

    suspend fun findRequirementResponseByIds(
        id: CommandId,
        params: FindRequirementResponseByIdsAction.Params
    ): Result<Reply<FindRequirementResponseByIdsAction.Result>, Fail.Incident>
}
