package com.procurement.orchestrator.domain.model.qualification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

class QualificationId private constructor(private val value: String) : Serializable {

    companion object {
        val pattern: String
            get() = UUID_PATTERN

        fun validation(text: String): Boolean = text.isUUID()

        @JvmStatic
        @JsonCreator
        fun tryCreateOrNull(text: String): QualificationId? = if (validation(text)) QualificationId(text) else null

        fun generate(): QualificationId = QualificationId(UUID.randomUUID().toString())
    }


    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is QualificationId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

}
