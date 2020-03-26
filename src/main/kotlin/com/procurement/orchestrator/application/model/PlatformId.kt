package com.procurement.orchestrator.application.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable

class PlatformId private constructor(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is PlatformId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        val pattern: String
            get() = UUID_PATTERN

        fun validation(text: String): Boolean = text.isUUID()

        @JvmStatic
        @JsonCreator
        fun tryCreateOrNull(text: String): PlatformId? = if (validation(text)) PlatformId(text) else null
    }
}
