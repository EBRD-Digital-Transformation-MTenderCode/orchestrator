package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction

object QualificationCommands {

    object CreateSubmission : CreateSubmissionAction()

    object ValidateSubmission : ValidateSubmissionAction()
}
