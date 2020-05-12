package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.GetSubmissionStateByIdsAction

object QualificationCommands {

    object CheckPeriod : CheckPeriodAction()

    object GetSubmissionStateByIds : GetSubmissionStateByIdsAction()

    object ValidateSubmission : ValidateSubmissionAction()
}
