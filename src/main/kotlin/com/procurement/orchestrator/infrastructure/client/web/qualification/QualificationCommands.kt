package com.procurement.orchestrator.infrastructure.client.web.qualification

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CheckDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.CreateQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DetermineNextsForQualificationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.DoDeclarationAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindRequirementResponseByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.StartQualificationPeriodAction

object QualificationCommands {

    object CreateSubmission : CreateSubmissionAction()

    object CheckPeriod : CheckPeriodAction()

    object ValidateSubmission : ValidateSubmissionAction()

    object StartQualificationPeriod : StartQualificationPeriodAction()

    object FindQualificationIds : FindQualificationIdsAction()

    object CheckDeclaration : CheckDeclarationAction()

    object CreateQualification : CreateQualificationAction()

    object DetermineNextsForQualification : DetermineNextsForQualificationAction()

    object DoDeclaration : DoDeclarationAction()

    object FindRequirementResponseByIds: FindRequirementResponseByIdsAction()
}
