package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class ValueBreakdown(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ValueBreakdownId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("type") @param:JsonProperty("type") val types: List<String> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("estimationMethod") @param:JsonProperty("estimationMethod") val estimationMethod: Value? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ValueBreakdown
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
