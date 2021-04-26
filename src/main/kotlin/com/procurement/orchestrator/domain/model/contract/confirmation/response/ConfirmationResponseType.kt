package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ConfirmationResponseType(@JsonValue override val key: String) : EnumElementProvider.Key {

    DOCUMENT("document"),
    HASH("hash");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConfirmationResponseType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConfirmationResponseType.orThrow(name)
    }
}