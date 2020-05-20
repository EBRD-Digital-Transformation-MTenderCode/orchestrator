package com.procurement.orchestrator.domain.model.can

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

class CanId(private val value: String) : Serializable {

    companion object {
        val pattern: String
            get() = UUID_PATTERN

        fun validation(text: String): Boolean = text.isUUID()

        @JvmStatic
        @JsonCreator
        fun tryCreateOrNull(text: String): CanId? = if (validation(text)) CanId(text) else null

        fun generate(): CanId = CanId(UUID.randomUUID().toString())
    }

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
}
