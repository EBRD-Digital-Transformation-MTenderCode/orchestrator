package com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.orchestrator.infrastructure.exception.conversion.coefficient.rate.CoefficientRateException
import java.io.IOException
import java.math.BigDecimal

class CoefficientRateDeserializer : JsonDeserializer<CoefficientRate>() {
    companion object {
        fun deserialize(value: BigDecimal): CoefficientRate = CoefficientRate(rate = value)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): CoefficientRate {
        return when (jsonParser.currentToken) {
            JsonToken.VALUE_NUMBER_INT,
            JsonToken.VALUE_NUMBER_FLOAT -> deserialize(BigDecimal(jsonParser.text))
            else -> throw CoefficientRateException(
                coefficient = jsonParser.text,
                description = "Invalid type. Number or Integer required"
            )
        }
    }
}
