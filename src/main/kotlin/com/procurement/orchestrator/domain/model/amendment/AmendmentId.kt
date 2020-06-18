package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable
import java.util.*

class AmendmentId private constructor(private val value: String) : Serializable {

    companion object {

        @JvmStatic
        @JsonCreator
        fun create(text: String): AmendmentId = AmendmentId(text)

        fun generate(): AmendmentId = AmendmentId(UUID.randomUUID().toString())
    }

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
}
