package com.procurement.orchestrator.domain.model.qualification

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.measure.Scoring
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import java.io.Serializable
import java.time.LocalDateTime

data class Qualification(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: QualificationId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: QualificationStatus? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedSubmission") @param:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)

    @field:JsonProperty("scoring") @param:JsonProperty("scoring") val scoring: Scoring? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses()

) : IdentifiableObject<Qualification>, Serializable {

    override fun updateBy(src: Qualification): Qualification = Qualification(
        id = id,
        date = src.date or date,
        status = src.status or status,
        statusDetails = src.statusDetails or statusDetails,
        relatedSubmission = src.relatedSubmission or relatedSubmission,
        scoring = src.scoring or scoring,
        requirementResponses = requirementResponses updateBy src.requirementResponses
    )
}
