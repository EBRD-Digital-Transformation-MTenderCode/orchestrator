package com.procurement.orchestrator.infrastructure.client.web.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
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
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpDossierClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    DossierClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun getSubmissionPeriodEndDate(
        id: CommandId,
        params: GetSubmissionPeriodEndDateAction.Params
    ): Result<Reply<GetSubmissionPeriodEndDateAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.GetSubmissionPeriodEndDate.build(id = id, params = params),
        target = DossierCommands.GetSubmissionPeriodEndDate.target
    )

    override suspend fun validateRequirementResponse(
        id: CommandId,
        params: ValidateRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.ValidateRequirementResponse.build(id = id, params = params)
    )

    override suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.CheckPeriod.build(id = id, params = params)
    )

    override suspend fun validateSubmission(
        id: CommandId,
        params: ValidateSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.ValidateSubmission.build(id = id, params = params)
    )

    override suspend fun createSubmission(
        id: CommandId,
        params: CreateSubmissionAction.Params
    ): Result<Reply<CreateSubmissionAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.CreateSubmission.build(id = id, params = params),
        target = DossierCommands.CreateSubmission.target
    )

    override suspend fun getSubmissionStateByIds(
        id: CommandId,
        params: GetSubmissionStateByIdsAction.Params
    ): Result<Reply<GetSubmissionStateByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.GetSubmissionStateByIds.build(id = id, params = params),
        target = DossierCommands.GetSubmissionStateByIds.target
    )

    override suspend fun checkAccessToSubmission(
        id: CommandId,
        params: CheckAccessToSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.CheckAccessToSubmission.build(id = id, params = params)
    )

    override suspend fun setStateForSubmission(
        id: CommandId,
        params: SetStateForSubmissionAction.Params
    ): Result<Reply<SetStateForSubmissionAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.SetStateForSubmission.build(id = id, params = params),
        target = DossierCommands.SetStateForSubmission.target
    )

    override suspend fun getOrganizations(
        id: CommandId,
        params: GetOrganizationsAction.Params
    ): Result<Reply<GetOrganizationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.GetOrganizations.build(id = id, params = params),
        target = DossierCommands.GetOrganizations.target
    )

    override suspend fun verifySubmissionPeriodEnd(
        id: CommandId,
        params: VerifySubmissionPeriodEndAction.Params
    ): Result<Reply<VerifySubmissionPeriodEndAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.VerifySubmissionPeriodEnd.build(id = id, params = params),
        target = DossierCommands.VerifySubmissionPeriodEnd.target
    )

    override suspend fun findSubmissions(
        id: CommandId,
        params: FindSubmissionsAction.Params
    ): Result<Reply<FindSubmissionsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.FindSubmissions.build(id = id, params = params),
        target = DossierCommands.FindSubmissions.target
    )

    override suspend fun finalizeSubmissions(
        id: CommandId,
        params: FinalizeSubmissionsAction.Params
    ): Result<Reply<FinalizeSubmissionsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.FinalizeSubmissions.build(id = id, params = params),
        target = DossierCommands.FinalizeSubmissions.target
    )

    override suspend fun getSubmissionCandidateReferencesByQualificationIds(
        id: CommandId,
        params: GetSubmissionCandidateReferencesByQualificationIdsAction.Params
    ): Result<Reply<GetSubmissionCandidateReferencesByQualificationIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.GetSubmissionCandidateReferencesByQualificationIds.build(id = id, params = params),
        target = DossierCommands.GetSubmissionCandidateReferencesByQualificationIds.target
    )

    override suspend fun getSubmissionsForTendering(
        id: CommandId,
        params: GetSubmissionsForTenderingAction.Params
    ): Result<Reply<GetSubmissionsForTenderingAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.GetSubmissionsForTendering.build(id = id, params = params),
        target = DossierCommands.GetSubmissionsForTendering.target
    )

    override suspend fun checkPresenceCandidateInOneSubmission(
        id: CommandId,
        params: CheckPresenceCandidateInOneSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = DossierCommands.CheckPresenceCandidateInOneSubmission.build(id = id, params = params)
    )
}
