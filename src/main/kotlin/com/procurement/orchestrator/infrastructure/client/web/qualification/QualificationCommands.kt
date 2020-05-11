package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction

object QualificationCommands {

    object CheckPeriod : CheckPeriodAction()

    object ValidateSubmission : ValidateSubmissionAction()
}
