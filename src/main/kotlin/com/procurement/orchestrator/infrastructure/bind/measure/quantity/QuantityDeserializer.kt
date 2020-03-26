package com.procurement.orchestrator.infrastructure.bind.measure.quantity

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.infrastructure.exception.measure.QuantityValueException
import java.io.IOException

class QuantityDeserializer : JsonDeserializer<Quantity>() {
    companion object {
        fun deserialize(text: String): Quantity = Quantity.tryCreate(text)
            .orThrow { error -> QuantityValueException(text, error.description) }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Quantity {
        if (jsonParser.currentToken != JsonToken.VALUE_NUMBER_FLOAT
            && jsonParser.currentToken != JsonToken.VALUE_NUMBER_INT
        ) {
            throw QuantityValueException(quantity = jsonParser.text, description = "The value must be a real number.")
        }
        return deserialize(jsonParser.text)
    }
}
