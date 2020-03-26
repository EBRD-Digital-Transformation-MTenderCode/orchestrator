package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountSerializer
import java.io.Serializable

//TODO create value object
data class ValueTax(
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
) : Serializable
