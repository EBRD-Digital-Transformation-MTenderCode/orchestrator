package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
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
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PersonesProcessingAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetStateForBidsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateBidDataAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction

interface SubmissionClient {

    suspend fun doInvitations(
        id: CommandId,
        params: DoInvitationsAction.Params
    ): Result<Reply<DoInvitationsAction.Result>, Fail.Incident>

    suspend fun publishInvitations(
        id: CommandId,
        params: PublishInvitationsAction.Params
    ): Result<Reply<PublishInvitationsAction.Result>, Fail.Incident>

    suspend fun checkAbsenceActiveInvitations(
        id: CommandId,
        params: CheckAbsenceActiveInvitationsAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkAccessToBid(
        id: CommandId,
        params: CheckAccessToBidAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun validateTenderPeriod(
        id: CommandId,
        params: ValidateTenderPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkBidState(
        id: CommandId,
        params: CheckBidStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun setTenderPeriodAction(
        id: CommandId,
        params: SetTenderPeriodAction.Params
    ): Result<Reply<SetTenderPeriodAction.Result>, Fail.Incident>

    suspend fun setStateForBids(
        id: CommandId,
        params: SetStateForBidsAction.Params
    ): Result<Reply<SetStateForBidsAction.Result>, Fail.Incident>

    suspend fun checkPeriod(id: CommandId, params: CheckPeriodAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun validateBidData(id: CommandId, params: ValidateBidDataAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun createBid(id: CommandId, params: CreateBidAction.Params): Result<Reply<CreateBidAction.Result>, Fail.Incident>

    suspend fun createInvitations(
        id: CommandId,
        params: CreateInvitationsAction.Params
    ): Result<Reply<CreateInvitationsAction.Result>, Fail.Incident>

    suspend fun finalizeBidsByAwards(id: CommandId, params: FinalizeBidsByAwardsAction.Params): Result<Reply<FinalizeBidsByAwardsAction.Result>, Fail.Incident>

    suspend fun getBidsForPacs(
        id: CommandId,
        params: GetBidsForPacsAction.Params
    ): Result<Reply<GetBidsForPacsAction.Result>, Fail.Incident>

    suspend fun getSuppliersOwners(
        id: CommandId,
        params: GetSuppliersOwnersAction.Params
    ): Result<Reply<GetSuppliersOwnersAction.Result>, Fail.Incident>

    suspend fun getOrganizationsByReferencesFromPacs(
        id: CommandId,
        params: GetOrganizationsByReferencesFromPacsAction.Params
    ): Result<Reply<GetOrganizationsByReferencesFromPacsAction.Result>, Fail.Incident>

    suspend fun findDocumentsByBidIds(
        id: CommandId,
        params: FindDocumentsByBidIdsAction.Params
    ): Result<Reply<FindDocumentsByBidIdsAction.Result>, Fail.Incident>

    suspend fun checkExistenceOfInvitation(
        id: CommandId,
        params: CheckExistenceOfInvitationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun personesProcessing(
        id: CommandId,
        params: PersonesProcessingAction.Params
    ): Result<Reply<PersonesProcessingAction.Result>, Fail.Incident>
}
