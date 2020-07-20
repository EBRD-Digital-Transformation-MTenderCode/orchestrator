package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction

object SubmissionCommands {

    object DoInvitations : DoInvitationsAction()

    object CheckAbsenceActiveInvitations : CheckAbsenceActiveInvitationsAction()

    object SetTenderPeriod: SetTenderPeriodAction()
}
