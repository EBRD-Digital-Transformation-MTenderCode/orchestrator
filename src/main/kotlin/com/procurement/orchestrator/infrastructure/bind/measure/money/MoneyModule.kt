package com.procurement.orchestrator.infrastructure.bind.measure.money

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.measure.Money

class MoneyModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Money::class.java, MoneySerializer())
        addDeserializer(Money::class.java, MoneyDeserializer())
    }
}
