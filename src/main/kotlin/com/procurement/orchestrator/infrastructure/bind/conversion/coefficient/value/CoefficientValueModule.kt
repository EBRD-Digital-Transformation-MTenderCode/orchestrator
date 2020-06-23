package com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientValue

class CoefficientValueModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(CoefficientValue::class.java, CoefficientValueSerializer())
        addDeserializer(CoefficientValue::class.java, CoefficientValueDeserializer())
    }
}
