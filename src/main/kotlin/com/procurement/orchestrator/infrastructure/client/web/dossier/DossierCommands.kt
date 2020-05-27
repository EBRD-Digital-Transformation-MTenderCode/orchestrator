package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetOrganizationsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.SetStateForSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction

object DossierCommands {

    object CheckAccessToSubmission : CheckAccessToSubmissionAction()

    object GetOrganizations : GetOrganizationsAction()

    object GetSubmissionPeriodEndDate : GetSubmissionPeriodEndDateAction()

    object GetSubmissionStateByIds : GetSubmissionStateByIdsAction()

    object CheckAccessToSubmission: CheckAccessToSubmissionAction()

    object SetStateForSubmission : SetStateForSubmissionAction()
    object ValidateRequirementResponse : ValidateRequirementResponseAction()
}
