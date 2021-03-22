package com.procurement.orchestrator.infrastructure.client.web.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAbsenceActiveInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAccessToBidAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckBidStateAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CreateBidAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FindDocumentsByBidIdsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetBidsForPacsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.GetOrganizationsByReferencesFromPacsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetStateForBidsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateBidDataAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpSubmissionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    SubmissionClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun doInvitations(
        id: CommandId,
        params: DoInvitationsAction.Params
    ): Result<Reply<DoInvitationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.DoInvitations.build(id = id, params = params),
        target = SubmissionCommands.DoInvitations.target
    )

    override suspend fun publishInvitations(
        id: CommandId,
        params: PublishInvitationsAction.Params
    ): Result<Reply<PublishInvitationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.PublishInvitations.build(id = id, params = params),
        target = SubmissionCommands.PublishInvitations.target
    )

    override suspend fun checkAbsenceActiveInvitations(
        id: CommandId,
        params: CheckAbsenceActiveInvitationsAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CheckAbsenceActiveInvitations.build(id = id, params = params)
    )

    override suspend fun checkAccessToBid(
        id: CommandId,
        params: CheckAccessToBidAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CheckAccessToBid.build(id = id, params = params)
    )


    override suspend fun validateTenderPeriod(
        id: CommandId,
        params: ValidateTenderPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.ValidateTenderPeriod.build(id = id, params = params)
    )

    override suspend fun checkBidState(
        id: CommandId,
        params: CheckBidStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CheckBidState.build(id = id, params = params)
    )

    override suspend fun setTenderPeriodAction(
        id: CommandId,
        params: SetTenderPeriodAction.Params
    ): Result<Reply<SetTenderPeriodAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.SetTenderPeriod.build(id = id, params = params),
        target = SubmissionCommands.SetTenderPeriod.target
    )

    override suspend fun setStateForBids(
        id: CommandId,
        params: SetStateForBidsAction.Params
    ): Result<Reply<SetStateForBidsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.SetStateForBids.build(id = id, params = params),
        target = SubmissionCommands.SetStateForBids.target
    )

    override suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CheckPeriod.build(id = id, params = params)
    )

    override suspend fun validateBidData(
        id: CommandId,
        params: ValidateBidDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.ValidateBidData.build(id = id, params = params)
    )

    override suspend fun createBid(
        id: CommandId,
        params: CreateBidAction.Params
    ): Result<Reply<CreateBidAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.CreateBid.build(id = id, params = params),
        target = SubmissionCommands.CreateBid.target
    )

    override suspend fun getBidsForPacs(
        id: CommandId,
        params: GetBidsForPacsAction.Params
    ): Result<Reply<GetBidsForPacsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.GetBidsForPacs.build(id = id, params = params),
        target = SubmissionCommands.GetBidsForPacs.target
    )

    override suspend fun getOrganizationsByReferencesFromPacs(
        id: CommandId,
        params: GetOrganizationsByReferencesFromPacsAction.Params
    ): Result<Reply<GetOrganizationsByReferencesFromPacsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.GetOrganizationsByReferencesFromPacs.build(id = id, params = params),
        target = SubmissionCommands.GetOrganizationsByReferencesFromPacs.target
    )

    override suspend fun findDocumentsByBidIds(
        id: CommandId,
        params: FindDocumentsByBidIdsAction.Params
    ): Result<Reply<FindDocumentsByBidIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = SubmissionCommands.FindDocumentsByBidIds.build(id = id, params = params),
        target = SubmissionCommands.FindDocumentsByBidIds.target
    )
}
