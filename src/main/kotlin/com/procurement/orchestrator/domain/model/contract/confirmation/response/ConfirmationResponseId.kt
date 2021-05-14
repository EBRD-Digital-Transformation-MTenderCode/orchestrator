package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable
import java.util.*

class ConfirmationResponseId private constructor(private val value: String) : Serializable {

    companion object {

        @JvmStatic
        @JsonCreator
        fun create(text: String): ConfirmationResponseId = ConfirmationResponseId(text)

        fun generate(): ConfirmationResponseId = ConfirmationResponseId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ConfirmationResponseId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value
}