package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class AmendmentRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {

    LOT("lot"),
    TENDER("tender");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentRelatesTo>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}

