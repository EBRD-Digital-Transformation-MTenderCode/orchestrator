package com.procurement.orchestrator.infrastructure.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable

class Version private constructor(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Version
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {

        private val regex = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\$".toRegex()

        val pattern: String
            get() = regex.pattern

        @JvmStatic
        @JsonCreator
        fun tryCreateOrNull(value: String): Version? = if (value.matches(regex)) Version(value = value) else null

        fun parse(value: String): Version = if (value.matches(regex))
            Version(value = value)
        else
            throw IllegalArgumentException("The parameter value contains invalid value '$value'. A value is being expected that matches the '$pattern' pattern.")
    }
}
