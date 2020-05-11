package com.procurement.orchestrator.domain.model.submission

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import java.io.Serializable
import java.time.LocalDateTime

data class Submission(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId.Permanent,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: SubmissionStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: Candidates = Candidates(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents()

) : IdentifiableObject<Submission>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Submission
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Submission) = Submission(
        id = id,
        owner = src.owner or owner,
        token = src.token or token,
        requirementResponses = requirementResponses updateBy src.requirementResponses,
        candidates = src.candidates updateBy candidates,
        documents = documents updateBy src.documents
    )
}
