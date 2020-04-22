package com.procurement.orchestrator.domain.model.can

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class CanStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    CONTRACT_PROJECT("contractProject"),
    UNSUCCESSFUL("unsuccessful"),
    EMPTY("empty"),
    ACTIVE("active");

    override fun toString(): String = key

    companion object : EnumElementProvider<CanStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CanStatusDetails.orThrow(name)
    }
}
