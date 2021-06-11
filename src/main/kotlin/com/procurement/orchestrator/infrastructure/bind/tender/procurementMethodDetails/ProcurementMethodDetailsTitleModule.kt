package com.procurement.orchestrator.infrastructure.bind.tender.procurementMethodDetails

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.ProcurementMethodDetailsTitle

class ProcurementMethodDetailsTitleModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ProcurementMethodDetailsTitle::class.java, ProcurementMethodDetailsTitleSerializer())
        addDeserializer(ProcurementMethodDetailsTitle::class.java, ProcurementMethodDetailsTitleDeserializer())
    }
}
