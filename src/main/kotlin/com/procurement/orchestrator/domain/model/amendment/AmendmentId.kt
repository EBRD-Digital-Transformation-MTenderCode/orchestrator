package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

sealed class AmendmentId(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is AmendmentId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    class Temporal private constructor(value: String) : AmendmentId(value), Serializable {
        companion object {
            fun create(text: String): AmendmentId = Temporal(text)
        }
    }

    class Permanent private constructor(value: String) : AmendmentId(value), Serializable {
        companion object {
            val pattern: String
                get() = UUID_PATTERN

            fun validation(text: String): Boolean = text.isUUID()

            fun tryCreateOrNull(text: String): AmendmentId? = if (validation(text)) Permanent(text) else null

            fun generate(): AmendmentId = Permanent(UUID.randomUUID().toString())
        }
    }
}
