package com.procurement.orchestrator.domain.model.contract.observation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Observation(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ObservationId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("notes") @param:JsonProperty("notes") val notes: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("measure") @param:JsonProperty("measure") val measure: Any? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: ObservationUnit? = null
) : IdentifiableObject<Observation>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Observation
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Observation) = Observation(
        id = id,
        notes = src.notes or notes,
        measure = src.measure or measure,
        unit = unit updateBy src.unit
    )
}
