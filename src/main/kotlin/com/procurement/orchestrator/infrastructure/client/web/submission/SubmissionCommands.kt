package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAccessToBidAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckBidStateAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckExistenceOfInvitationAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CreateBidAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CreateInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FinalizeBidsByAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FindDocumentsByBidIdsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetBidsForPacsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetOrganizationsByReferencesFromPacsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetSuppliersOwnersAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetStateForBidsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateBidDataAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction

object SubmissionCommands {

    object DoInvitations : DoInvitationsAction()

    object PublishInvitations : PublishInvitationsAction()

    object CheckAbsenceActiveInvitations : CheckAbsenceActiveInvitationsAction()

    object CheckAccessToBid : CheckAccessToBidAction()

    object ValidateTenderPeriod : ValidateTenderPeriodAction()

    object CheckBidState : CheckBidStateAction()

    object SetTenderPeriod: SetTenderPeriodAction()

    object SetStateForBids: SetStateForBidsAction()

    object CheckPeriod : CheckPeriodAction()

    object ValidateBidData: ValidateBidDataAction()

    object CreateBid: CreateBidAction()

    object CreateInvitations: CreateInvitationsAction()

    object FinalizeBidsByAwards: FinalizeBidsByAwardsAction()

    object GetBidsForPacs: GetBidsForPacsAction()

    object GetSuppliersOwners: GetSuppliersOwnersAction()

    object GetOrganizationsByReferencesFromPacs: GetOrganizationsByReferencesFromPacsAction()

    object FindDocumentsByBidIds: FindDocumentsByBidIdsAction()

    object CheckExistenceOfInvitation: CheckExistenceOfInvitationAction()
}
