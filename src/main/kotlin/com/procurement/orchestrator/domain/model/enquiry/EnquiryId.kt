package com.procurement.orchestrator.domain.model.enquiry

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable

class EnquiryId private constructor(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is EnquiryId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {

        @JvmStatic
        @JsonCreator
        fun create(text: String): EnquiryId = EnquiryId(text)
    }
}
