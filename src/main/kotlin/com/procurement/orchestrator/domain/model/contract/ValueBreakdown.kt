package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class ValueBreakdown(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ValueBreakdownId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("type") @param:JsonProperty("type") val types: ValueBreakdownTypes = ValueBreakdownTypes(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("estimationMethod") @param:JsonProperty("estimationMethod") val estimationMethod: Value? = null
) : IdentifiableObject<ValueBreakdown>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ValueBreakdown
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: ValueBreakdown) = ValueBreakdown(
        id = id,
        types = types combineBy src.types,
        description = src.description or description,
        amount = src.amount or amount,
        estimationMethod = src.estimationMethod or estimationMethod
    )
}
