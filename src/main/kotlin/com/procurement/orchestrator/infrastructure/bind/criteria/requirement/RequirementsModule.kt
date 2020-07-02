package com.procurement.orchestrator.infrastructure.bind.criteria.requirement

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements

class RequirementsModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Requirements::class.java, RequirementsSerializer())
        addDeserializer(Requirements::class.java, RequirementsDeserializer())
    }
}
