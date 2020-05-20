package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class AgreedMetric(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AgreedMetricId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: Observations = Observations()
) : IdentifiableObject<AgreedMetric>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is AgreedMetric
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: AgreedMetric) = AgreedMetric(
        id = id,
        title = src.title or title,
        description = src.description or description,
        observations = observations updateBy src.observations
    )
}
