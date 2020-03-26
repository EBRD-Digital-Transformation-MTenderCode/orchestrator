package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class LotStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    AWARDED("awarded"),
    CANCELLED("cancelled"),
    EMPTY("empty"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<LotStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
