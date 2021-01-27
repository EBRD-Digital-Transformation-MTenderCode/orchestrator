package com.procurement.orchestrator.domain.model.requirement

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class RequirementStatus(@JsonValue override val key: String) : EnumElementProvider.Key {

    ACTIVE("active");

    override fun toString(): String = key

    companion object : EnumElementProvider<RequirementStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
