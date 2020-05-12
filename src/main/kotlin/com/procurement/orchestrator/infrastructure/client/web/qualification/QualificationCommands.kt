package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.SetStateForSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction

object QualificationCommands {

    object CreateSubmission : CreateSubmissionAction()

    object CheckPeriod : CheckPeriodAction()

    object ValidateSubmission : ValidateSubmissionAction()

    object SetStateForSubmission : SetStateForSubmissionAction()
}
