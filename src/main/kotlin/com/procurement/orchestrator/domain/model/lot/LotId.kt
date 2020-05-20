package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

sealed class LotId(private val value: String) : Serializable {

    companion object {

        @JvmStatic
        @JsonCreator
        fun parse(text: String): LotId? = Permanent.tryCreateOrNull(text)
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is LotId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    class Temporal private constructor(value: String) : LotId(value), Serializable {
        companion object {
            fun create(text: String): LotId = Temporal(text)
        }
    }

    class Permanent private constructor(value: String) : LotId(value), Serializable {
        companion object {
            val pattern: String
                get() = UUID_PATTERN

            fun validation(text: String): Boolean = text.isUUID()

            fun tryCreateOrNull(text: String): LotId? = if (validation(text)) Permanent(text) else null

            fun generate(): LotId = Permanent(UUID.randomUUID().toString())
        }
    }
}
