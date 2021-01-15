package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.evidence.EvidenceId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class ValidateSubmissionAction : ProceduralAction<ValidateSubmissionAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateSubmission"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
        @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: Candidates,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
    ) {
        data class RequirementResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatedCandidate") @field:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("evidences") @field:JsonProperty("evidences") val evidences: List<Evidence>?
        ) {
            data class Requirement(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
            )

            data class RelatedCandidate(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )

            data class Evidence(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: EvidenceId,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedDocument") @field:JsonProperty("relatedDocument") val relatedDocument: RelatedDocument?
            ) {
                data class RelatedDocument(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId
                )
            }
        }
    }
}
