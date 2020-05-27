package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.StartQualificationPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction

interface QualificationClient {


    suspend fun startQualificationPeriod(
        id: CommandId,
        params: StartQualificationPeriodAction.Params
    ): Result<Reply<StartQualificationPeriodAction.Result>, Fail.Incident>
}
