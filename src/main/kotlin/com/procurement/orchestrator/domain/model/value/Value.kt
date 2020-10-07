package com.procurement.orchestrator.domain.model.value

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountSerializer
import java.io.Serializable

data class Value(
    @param:JsonDeserialize(using = AmountDeserializer::class)
    @field:JsonSerialize(using = AmountSerializer::class)
    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Amount,

    @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String,

    @param:JsonDeserialize(using = AmountDeserializer::class)
    @field:JsonSerialize(using = AmountSerializer::class)
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amountNet") @param:JsonProperty("amountNet") val amountNet: Amount? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("valueAddedTaxIncluded") @param:JsonProperty("valueAddedTaxIncluded") val valueAddedTaxIncluded: Boolean? = null
) : ComplexObject<Value>, Serializable {

    override fun updateBy(src: Value) = Value(
        amount = src.amount or amount,
        currency = src.currency or currency,
        amountNet = src.amountNet or amountNet,
        valueAddedTaxIncluded = src.valueAddedTaxIncluded or valueAddedTaxIncluded
    )
}
