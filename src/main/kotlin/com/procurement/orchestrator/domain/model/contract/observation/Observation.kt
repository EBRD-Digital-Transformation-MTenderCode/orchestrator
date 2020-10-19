package com.procurement.orchestrator.domain.model.contract.observation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Observation(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ObservationId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("notes") @param:JsonProperty("notes") val notes: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("measure") @param:JsonProperty("measure") val measure: Any? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dimensions") @param:JsonProperty("dimensions") val dimensions: Dimensions?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedRequirementId") @param:JsonProperty("relatedRequirementId") val relatedRequirementId: String?


) : IdentifiableObject<Observation>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Observation
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Observation) = Observation(
        id = id,
        period = src.period or period,
        notes = src.notes or notes,
        measure = src.measure or measure,
        unit = unit updateBy src.unit,
        dimensions = src.dimensions or dimensions,
        relatedRequirementId = src.relatedRequirementId or relatedRequirementId
    )
}
