package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable

class ProcessDefinitionKey private constructor(private val value: String) : Serializable {
    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ProcessDefinitionKey
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        operator fun invoke(key: String): ProcessDefinitionKey = ProcessDefinitionKey(value = key)
    }
}
