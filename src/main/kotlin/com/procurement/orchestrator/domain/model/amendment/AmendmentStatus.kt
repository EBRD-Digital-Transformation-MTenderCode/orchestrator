package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class AmendmentStatus(@JsonValue override val key: String) : EnumElementProvider.Key {

    ACTIVE("active"),
    CANCELLED("cancelled"),
    PENDING("pending"),
    WITHDRAWN("withdrawn");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
