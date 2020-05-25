package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

object DossierCommands {

    object ValidateRequirementResponse : ValidateRequirementResponseAction()

    object CheckAccessToSubmission: CheckAccessToSubmissionAction()
}
