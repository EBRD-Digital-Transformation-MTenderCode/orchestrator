package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckPresenceCandidateInOneSubmissionAction : ProceduralAction<CheckPresenceCandidateInOneSubmissionAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkPresenceCandidateInOneSubmission"

    class Params(
        @param:JsonProperty("submissions") @field:JsonProperty("submissions") val submissions: Submissions
    ) {
        data class Submissions(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: SubmissionId,
                @param:JsonProperty("candidates") @field:JsonProperty("candidates") val candidates: List<Candidate>
            ) {
                data class Candidate(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId
                )
            }
        }
    }
}
