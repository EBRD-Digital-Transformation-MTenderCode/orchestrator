package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class LotGroup(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: LotGroupId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList(),

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("optionToCombine") @param:JsonProperty("optionToCombine") val optionToCombine: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maximumValue") @param:JsonProperty("maximumValue") val maximumValue: Value? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is LotGroup
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
