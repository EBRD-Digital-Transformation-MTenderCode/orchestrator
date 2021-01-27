package com.procurement.orchestrator.domain.model.requirement.response.evidence

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable
import java.util.*

class EvidenceId private constructor(private val value: String) : Serializable {

    companion object {

        @JvmStatic
        @JsonCreator
        fun create(text: String): EvidenceId = EvidenceId(text)

        fun generate(): EvidenceId = EvidenceId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is EvidenceId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value
}
