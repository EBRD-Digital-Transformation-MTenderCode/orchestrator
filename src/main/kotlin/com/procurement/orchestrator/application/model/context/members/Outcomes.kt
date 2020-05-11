package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import java.io.Serializable

class Outcomes(values: MutableMap<PlatformId, Details> = mutableMapOf()) :
    MutableMap<PlatformId, Outcomes.Details> by values, Serializable {

    data class Details(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: List<Submissions> = emptyList()

    ) : Serializable {

        data class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token?
        ) : Serializable

        data class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId
        ) : Serializable

        data class Submissions(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId.Permanent,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
        ) : Serializable
    }
}
