package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsAction

object SubmissionCommands {

    object DoInvitations : DoInvitationsAction()

    object PublishInvitations : PublishInvitationsAction()

    object CheckAbsenceActiveInvitations : CheckAbsenceActiveInvitationsAction()

    object ValidateTenderPeriod : ValidateTenderPeriodAction()
}
