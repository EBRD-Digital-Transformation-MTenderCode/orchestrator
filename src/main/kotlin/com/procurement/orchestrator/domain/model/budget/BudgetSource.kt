package com.procurement.orchestrator.domain.model.budget

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountSerializer
import java.io.Serializable

data class BudgetSource(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: BudgetSourceId,

    @param:JsonDeserialize(using = AmountDeserializer::class)
    @field:JsonSerialize(using = AmountSerializer::class)
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Amount? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is BudgetSource
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
