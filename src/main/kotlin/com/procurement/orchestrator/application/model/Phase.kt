package com.procurement.orchestrator.application.model

import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable

class Phase(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Phase
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value
}
