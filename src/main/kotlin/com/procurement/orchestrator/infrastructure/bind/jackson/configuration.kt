package com.procurement.orchestrator.infrastructure.bind.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeModule
import com.procurement.orchestrator.infrastructure.bind.measure.amount.AmountModule
import com.procurement.orchestrator.infrastructure.bind.measure.money.MoneyModule
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantityModule

fun ObjectMapper.configuration() {

    this.registerModule(KotlinModule())
    this.registerModule(MoneyModule())
    this.registerModule(AmountModule())
    this.registerModule(QuantityModule())
    this.registerModule(JsonDateTimeModule())

    this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    this.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    this.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}
