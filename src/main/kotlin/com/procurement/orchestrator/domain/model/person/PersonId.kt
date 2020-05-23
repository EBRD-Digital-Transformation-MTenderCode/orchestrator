package com.procurement.orchestrator.domain.model.person

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.identifier.IdentifierId
import com.procurement.orchestrator.domain.model.identifier.IdentifierScheme
import java.io.Serializable

class PersonId private constructor(private val value: String) : Serializable {

    @JsonValue
    override fun toString(): String = value

    companion object {

        @JvmStatic
        @JsonCreator
        fun parse(text: String): PersonId? = if (text.isBlank())
            null
        else
            PersonId(text)

        fun generate(scheme: IdentifierScheme, id: IdentifierId): PersonId =
            PersonId("$scheme-$id")
    }
}
