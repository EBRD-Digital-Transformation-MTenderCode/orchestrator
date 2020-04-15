package com.procurement.orchestrator.domain.model.can

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class CanStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<CanStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CanStatusDetails.orThrow(name)
    }
}
