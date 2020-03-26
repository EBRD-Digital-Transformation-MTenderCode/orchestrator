package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import java.io.Serializable

class Outcomes(values: MutableMap<PlatformId, Details> = mutableMapOf()) :
    MutableMap<PlatformId, Outcomes.Details> by values, Serializable {

    data class Details(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList()
    ) : Serializable {

        data class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("X-TOKEN") @param:JsonProperty("X-TOKEN") val token: Token?
        ) : Serializable
    }
}
