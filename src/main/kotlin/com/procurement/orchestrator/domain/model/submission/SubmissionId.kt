package com.procurement.orchestrator.domain.model.submission

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

sealed class SubmissionId(private val value: String) : Serializable {

    companion object {

        @JvmStatic
        @JsonCreator
        fun parse(text: String): SubmissionId? = Permanent.tryCreateOrNull(text)
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is SubmissionId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    class Temporal private constructor(value: String) : SubmissionId(value), Serializable {
        companion object {
            fun create(text: String): SubmissionId = Temporal(text)
        }
    }

    class Permanent private constructor(value: String) : SubmissionId(value), Serializable {
        companion object {
            val pattern: String
                get() = UUID_PATTERN

            fun validation(text: String): Boolean = text.isUUID()

            fun tryCreateOrNull(text: String): SubmissionId? = if (validation(text)) Permanent(text) else null

            fun generate(): SubmissionId = Permanent(UUID.randomUUID().toString())
        }
    }
}
