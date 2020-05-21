package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

object DossierCommands {

    object GetSubmissionPeriodEndDate : GetSubmissionPeriodEndDateAction()

    object ValidateRequirementResponse : ValidateRequirementResponseAction()
}
