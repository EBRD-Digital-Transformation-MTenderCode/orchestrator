package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsInPcrAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction

object SubmissionCommands {

    object DoInvitations : DoInvitationsAction()

    object PublishInvitations : PublishInvitationsAction()

    object CheckAbsenceActiveInvitations : CheckAbsenceActiveInvitationsAction()

    object ValidateTenderPeriod : ValidateTenderPeriodAction()

    object SetTenderPeriod: SetTenderPeriodAction()

    object PublishInvitationsInPcr: PublishInvitationsInPcrAction()
}
