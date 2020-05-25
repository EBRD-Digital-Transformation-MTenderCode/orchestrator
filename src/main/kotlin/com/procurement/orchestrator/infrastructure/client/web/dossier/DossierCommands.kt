package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.SetStateForSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

object DossierCommands {

    object GetSubmissionPeriodEndDate : GetSubmissionPeriodEndDateAction()

    object ValidateRequirementResponse : ValidateRequirementResponseAction()

    object GetSubmissionStateByIds : GetSubmissionStateByIdsAction()

    object CheckAccessToSubmission: CheckAccessToSubmissionAction()

    object SetStateForSubmission : SetStateForSubmissionAction()
}
