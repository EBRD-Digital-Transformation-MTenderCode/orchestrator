package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.organization.OrganizationReferences
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.SubmissionStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateSubmissionAction : FunctionalAction<CreateSubmissionAction.Params, CreateSubmissionAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createSubmission"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("submission") @param:JsonProperty("submission") val submission: Submission
    ) {
        data class Submission(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses(),

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: Candidates = Candidates(),

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents()
        )
    }

    class Result(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: SubmissionStatus,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses(),
        @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: OrganizationReferences,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents()
    ) : Serializable
}
