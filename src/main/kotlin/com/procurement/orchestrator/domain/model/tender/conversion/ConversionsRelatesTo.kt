package com.procurement.orchestrator.domain.model.tender.conversion

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ConversionsRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {

    REQUIREMENT("requirement");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConversionsRelatesTo>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
