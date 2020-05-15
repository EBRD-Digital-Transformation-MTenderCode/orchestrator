package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

object DossierCommands {

    object ValidateRequirementResponse : ValidateRequirementResponseAction()

    object GetSubmissionStateByIds : GetSubmissionStateByIdsAction()
}
