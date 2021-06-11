package com.procurement.orchestrator.infrastructure.bind.tender.procurementMethodDetails

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.orchestrator.domain.model.ProcurementMethodDetailsTitle
import java.io.IOException

class ProcurementMethodDetailsTitleDeserializer : JsonDeserializer<ProcurementMethodDetailsTitle>() {
    companion object {
        fun deserialize(text: String): ProcurementMethodDetailsTitle = ProcurementMethodDetailsTitle(text)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): ProcurementMethodDetailsTitle = deserialize(jsonParser.text)
}
