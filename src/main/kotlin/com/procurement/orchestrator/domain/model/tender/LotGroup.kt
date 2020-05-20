package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class LotGroup(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: LotGroupId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: RelatedLots = RelatedLots(),

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("optionToCombine") @param:JsonProperty("optionToCombine") val optionToCombine: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maximumValue") @param:JsonProperty("maximumValue") val maximumValue: Value? = null
) : IdentifiableObject<LotGroup>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is LotGroup
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: LotGroup) = LotGroup(
        id = id,
        relatedLots = relatedLots combineBy src.relatedLots,
        optionToCombine = src.optionToCombine or optionToCombine,
        maximumValue = src.maximumValue or maximumValue
    )
}
