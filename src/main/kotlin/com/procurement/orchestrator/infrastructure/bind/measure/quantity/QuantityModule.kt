package com.procurement.orchestrator.infrastructure.bind.measure.quantity

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.measure.Quantity

class QuantityModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Quantity::class.java, QuantitySerializer())
        addDeserializer(Quantity::class.java, QuantityDeserializer())
    }
}
