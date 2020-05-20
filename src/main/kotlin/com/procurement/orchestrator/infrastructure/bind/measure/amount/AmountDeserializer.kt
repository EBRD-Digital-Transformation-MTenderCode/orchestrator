package com.procurement.orchestrator.infrastructure.bind.measure.amount

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.infrastructure.exception.measure.AmountValueException
import java.io.IOException

class AmountDeserializer : JsonDeserializer<Amount>() {
    companion object {
        fun deserialize(text: String): Amount = Amount.tryCreate(text)
            .orThrow { error -> AmountValueException(text, error.description) }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Amount {
        if (jsonParser.currentToken != JsonToken.VALUE_NUMBER_FLOAT
            && jsonParser.currentToken != JsonToken.VALUE_NUMBER_INT
        ) {
            throw AmountValueException(amount = jsonParser.text, description = "The value must be a real number.")
        }
        return deserialize(jsonParser.text)
    }
}
