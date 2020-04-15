package com.procurement.orchestrator.domain.model.can

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

sealed class CanId(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is CanId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    class Temporal private constructor(value: String) : CanId(value), Serializable {
        companion object {
            fun create(text: String): CanId = Temporal(text)
        }
    }

    class Permanent private constructor(value: String) : CanId(value), Serializable {
        companion object {
            val pattern: String
                get() = UUID_PATTERN

            fun validation(text: String): Boolean = text.isUUID()

            @JvmStatic
            @JsonCreator
            fun tryCreateOrNull(text: String): CanId? = if (validation(text)) Permanent(text) else null

            fun generate(): CanId = Permanent(UUID.randomUUID().toString())
        }
    }
}
