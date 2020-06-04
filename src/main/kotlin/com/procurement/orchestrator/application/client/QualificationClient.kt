package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
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

    suspend fun createQualification(
        id: CommandId,
        params: CreateQualificationAction.Params
    ): Result<Reply<CreateQualificationAction.Result>, Fail.Incident>
}
