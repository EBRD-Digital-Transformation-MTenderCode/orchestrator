package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckAccessToSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CreateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FindSubmissionsForOpeningAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetOrganizationsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionPeriodEndDateAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.SetStateForSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateSubmissionAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.ValidateTenderPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.VerifySubmissionPeriodEndAction

interface DossierClient {

    suspend fun checkAccessToSubmission(
        id: CommandId,
        params: CheckAccessToSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getOrganizations(
        id: CommandId,
        params: GetOrganizationsAction.Params
    ): Result<Reply<GetOrganizationsAction.Result>, Fail.Incident>

    suspend fun getSubmissionPeriodEndDate(
        id: CommandId,
        params: GetSubmissionPeriodEndDateAction.Params
    ): Result<Reply<GetSubmissionPeriodEndDateAction.Result>, Fail.Incident>

    suspend fun getSubmissionStateByIds(
        id: CommandId,
        params: GetSubmissionStateByIdsAction.Params
    ): Result<Reply<GetSubmissionStateByIdsAction.Result>, Fail.Incident>

    suspend fun validateRequirementResponse(
        id: CommandId,
        params: ValidateRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkPeriod(
        id: CommandId,
        params: CheckPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun validateSubmission(
        id: CommandId,
        params: ValidateSubmissionAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createSubmission(
        id: CommandId,
        params: CreateSubmissionAction.Params
    ): Result<Reply<CreateSubmissionAction.Result>, Fail.Incident>

    suspend fun setStateForSubmission(
        id: CommandId,
        params: SetStateForSubmissionAction.Params
    ): Result<Reply<SetStateForSubmissionAction.Result>, Fail.Incident>

    suspend fun verifySubmissionPeriodEnd(
        id: CommandId,
        params: VerifySubmissionPeriodEndAction.Params
    ): Result<Reply<VerifySubmissionPeriodEndAction.Result>, Fail.Incident>

    suspend fun findSubmissionsForOpening(
        id: CommandId,
        params: FindSubmissionsForOpeningAction.Params
    ): Result<Reply<FindSubmissionsForOpeningAction.Result>, Fail.Incident>

    suspend fun validateTenderPeriod(
        id: CommandId,
        params: ValidateTenderPeriodAction.Params
    ): Result<Reply<Unit>, Fail.Incident>
}
