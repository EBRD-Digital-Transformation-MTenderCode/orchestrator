package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class LotStatus(@JsonValue override val key: String) : EnumElementProvider.Key {

    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    PLANNED("planned"),
    PLANNING("planning"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<LotStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
