package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcurementCategory(@JsonValue override val key: String) : EnumElementProvider.Key {

    GOODS("goods"),
    WORKS("works"),
    SERVICES("services");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementCategory>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
