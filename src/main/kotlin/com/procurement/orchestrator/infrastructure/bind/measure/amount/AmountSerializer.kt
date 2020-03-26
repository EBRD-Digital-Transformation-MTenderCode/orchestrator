package com.procurement.orchestrator.infrastructure.bind.measure.amount

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.orchestrator.domain.model.measure.Amount
import java.io.IOException

class AmountSerializer : JsonSerializer<Amount>() {
    companion object {
        fun serialize(amount: Amount): String = "%.2f".format(amount.value)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(amount: Amount, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeNumber(serialize(amount))
}
