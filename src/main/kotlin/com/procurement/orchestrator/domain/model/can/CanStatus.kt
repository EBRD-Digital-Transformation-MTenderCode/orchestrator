package com.procurement.orchestrator.domain.model.can

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class CanStatus(@JsonValue override val key: String) : EnumElementProvider.Key {

    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CANCELLED("cancelled");

    override fun toString(): String = key

    companion object : EnumElementProvider<CanStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CanStatus.orThrow(name)
    }
}
