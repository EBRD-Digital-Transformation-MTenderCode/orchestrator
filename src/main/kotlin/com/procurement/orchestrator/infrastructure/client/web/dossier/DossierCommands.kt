package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPresenceCandidateInOneSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FinalizeSubmissionsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FindSubmissionsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetOrganizationsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionCandidateReferencesByQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionsForTenderingAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.SetStateForSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.VerifySubmissionPeriodEndAction

object DossierCommands {

    object GetOrganizations : GetOrganizationsAction()

    object GetSubmissionPeriodEndDate : GetSubmissionPeriodEndDateAction()

    object GetSubmissionStateByIds : GetSubmissionStateByIdsAction()

    object CheckAccessToSubmission : CheckAccessToSubmissionAction()

    object SetStateForSubmission : SetStateForSubmissionAction()

    object ValidateRequirementResponse : ValidateRequirementResponseAction()

    object CheckPeriod : CheckPeriodAction()

    object ValidateSubmission : ValidateSubmissionAction()

    object CreateSubmission : CreateSubmissionAction()

    object VerifySubmissionPeriodEnd : VerifySubmissionPeriodEndAction()

    object FindSubmissions : FindSubmissionsAction()

    object FinalizeSubmissions : FinalizeSubmissionsAction()

    object GetSubmissionCandidateReferencesByQualificationIds : GetSubmissionCandidateReferencesByQualificationIdsAction()

    object GetSubmissionsForTendering : GetSubmissionsForTenderingAction()

    object CheckPresenceCandidateInOneSubmission: CheckPresenceCandidateInOneSubmissionAction()
}
