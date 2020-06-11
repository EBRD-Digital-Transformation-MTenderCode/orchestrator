package com.procurement.orchestrator.infrastructure.bind.measure.scoring

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.measure.Scoring

class ScoringModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Scoring::class.java, ScoringSerializer())
        addDeserializer(Scoring::class.java, ScoringDeserializer())
    }
}
