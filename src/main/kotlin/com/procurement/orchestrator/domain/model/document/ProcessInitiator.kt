package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcessInitiator(@JsonValue override val key: String) : EnumElementProvider.Key {

    ISSUING_FRAMEWORK_CONTRACT("issuingFrameworkContract"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcessInitiator>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
