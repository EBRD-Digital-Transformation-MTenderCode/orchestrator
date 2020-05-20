package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class AmendmentType(@JsonValue override val key: String) : EnumElementProvider.Key {

    CANCELLATION("cancellation"),
    TENDER_CHANGE("tenderChange");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
