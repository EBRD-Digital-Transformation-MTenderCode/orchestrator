package com.procurement.orchestrator.domain.model.requirement.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

sealed class RequirementResponseId(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is RequirementResponseId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    class Temporal private constructor(value: String) : RequirementResponseId(value), Serializable {
        companion object {
            fun create(text: String): RequirementResponseId = Temporal(text)
        }
    }

    class Permanent private constructor(value: String) : RequirementResponseId(value), Serializable {
        companion object {
            val pattern: String
                get() = UUID_PATTERN

            fun validation(text: String): Boolean = text.isUUID()

            @JvmStatic
            @JsonCreator
            fun tryCreateOrNull(text: String): RequirementResponseId? = if (validation(text)) Permanent(text) else null

            fun generate(): RequirementResponseId = Permanent(UUID.randomUUID().toString())
        }
    }
}
