package com.procurement.orchestrator.infrastructure.bind.date

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.orchestrator.domain.extension.date.format
import java.time.LocalDateTime

class JsonDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    companion object {
        fun serialize(date: LocalDateTime): String = date.format()
    }

    override fun serialize(date: LocalDateTime, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(date))
}
