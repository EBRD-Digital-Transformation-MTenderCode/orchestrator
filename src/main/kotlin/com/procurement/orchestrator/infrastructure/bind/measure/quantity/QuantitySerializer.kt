package com.procurement.orchestrator.infrastructure.bind.measure.quantity

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.orchestrator.domain.model.measure.Quantity
import java.io.IOException

class QuantitySerializer : JsonSerializer<Quantity>() {
    companion object {
        fun serialize(quantity: Quantity): String = "%.3f".format(quantity.value)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(quantity: Quantity, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeNumber(serialize(quantity))
}
