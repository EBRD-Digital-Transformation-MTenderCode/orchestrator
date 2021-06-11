package com.procurement.orchestrator.infrastructure.bind.tender.procurementMethodDetails

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.orchestrator.domain.model.ProcurementMethodDetailsTitle
import java.io.IOException

class ProcurementMethodDetailsTitleSerializer : JsonSerializer<ProcurementMethodDetailsTitle>() {
    companion object {
        fun serialize(title: ProcurementMethodDetailsTitle): String = title.value
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
        title: ProcurementMethodDetailsTitle,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) = jsonGenerator.writeString(serialize(title))
}
