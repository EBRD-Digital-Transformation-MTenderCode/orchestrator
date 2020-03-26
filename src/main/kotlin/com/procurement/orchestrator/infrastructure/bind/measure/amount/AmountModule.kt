package com.procurement.orchestrator.infrastructure.bind.measure.amount

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.measure.Amount

class AmountModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Amount::class.java, AmountSerializer())
        addDeserializer(Amount::class.java, AmountDeserializer())
    }
}
