package com.procurement.orchestrator.domain.model.tender.target

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Target(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: TargetId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: Observations = Observations()

) : IdentifiableObject<Target>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Target
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Target) = Target(
        id = id,
        title = src.title or title,
        relatesTo = src.relatesTo or relatesTo,
        relatedItem = src.relatedItem or relatedItem,
        observations = src.observations updateBy observations
    )
}
