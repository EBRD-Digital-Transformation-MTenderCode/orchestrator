package com.procurement.orchestrator.domain.model.tender.criteria

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class CriteriaRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {

    AWARD("award"),
    ITEM("item"),
    LOT("lot"),
    TENDERER("tenderer");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriteriaRelatesTo>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
