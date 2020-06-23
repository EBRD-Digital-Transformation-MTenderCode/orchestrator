package com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientRate

class RequirementValueModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(RequirementResponseValue::class.java, RequirementValueSerializer())
        addDeserializer(RequirementResponseValue::class.java, RequirementValueDeserializer())
    }
}
