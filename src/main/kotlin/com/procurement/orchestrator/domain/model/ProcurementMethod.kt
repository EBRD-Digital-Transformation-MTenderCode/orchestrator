package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcurementMethod (@JsonValue override val key: String) : EnumElementProvider.Key {
    SELECTIVE("selective"),
    LIMITED("limited"),
    OPEN("open");

    companion object : EnumElementProvider<ProcurementMethodDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethodDetails.orThrow(name)
    }
}