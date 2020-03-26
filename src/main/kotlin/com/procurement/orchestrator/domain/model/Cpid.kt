package com.procurement.orchestrator.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.extension.date.toMilliseconds
import com.procurement.orchestrator.domain.model.address.country.CountryId
import java.io.Serializable
import java.time.LocalDateTime

class Cpid private constructor(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Cpid
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}\$".toRegex()

        val pattern: String
            get() = regex.pattern

        @JvmStatic
        @JsonCreator
        fun tryCreateOrNull(value: String): Cpid? = if (value.matches(regex)) Cpid(value = value) else null

        fun generate(prefix: String, country: CountryId, timestamp: LocalDateTime): Cpid =
            Cpid("${prefix.toLowerCase()}-${country.toUpperCase()}-${timestamp.toMilliseconds()}")
    }
}
