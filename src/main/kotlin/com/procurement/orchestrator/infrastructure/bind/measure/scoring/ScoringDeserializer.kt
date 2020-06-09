package com.procurement.orchestrator.infrastructure.bind.measure.scoring

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.orchestrator.domain.model.measure.Scoring
import com.procurement.orchestrator.infrastructure.exception.measure.ScoringValueException
import java.io.IOException

class ScoringDeserializer : JsonDeserializer<Scoring>() {
    companion object {
        fun deserialize(text: String): Scoring = Scoring.tryCreate(text)
            .orThrow { error -> ScoringValueException(text, error.description) }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Scoring {
        if (jsonParser.currentToken != JsonToken.VALUE_NUMBER_FLOAT
            && jsonParser.currentToken != JsonToken.VALUE_NUMBER_INT
        ) {
            throw ScoringValueException(scoring = jsonParser.text, description = "The value must be a real number.")
        }
        return deserialize(jsonParser.text)
    }
}
