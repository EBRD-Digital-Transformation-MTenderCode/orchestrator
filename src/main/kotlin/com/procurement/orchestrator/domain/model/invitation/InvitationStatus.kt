package com.procurement.orchestrator.domain.model.invitation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class InvitationStatus(@JsonValue override val key: String) : EnumElementProvider.Key {

    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled");

    override fun toString(): String = key

    companion object : EnumElementProvider<InvitationStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
