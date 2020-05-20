package com.procurement.orchestrator.infrastructure.bind.date

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.orchestrator.domain.extension.date.parseLocalDateTime
import java.time.LocalDateTime

class JsonDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    companion object {
        fun deserialize(value: String): LocalDateTime = value.parseLocalDateTime()
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LocalDateTime =
        deserialize(jsonParser.text)
}
