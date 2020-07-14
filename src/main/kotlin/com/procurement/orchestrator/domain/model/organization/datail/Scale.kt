package com.procurement.orchestrator.domain.model.organization.datail

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class Scale(@JsonValue override val key: String) : EnumElementProvider.Key {

    EMPTY(""),
    LARGE("large"),
    MICRO("micro"),
    SME("sme");

    override fun toString(): String = key

    companion object : EnumElementProvider<Scale>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
