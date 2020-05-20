package com.procurement.orchestrator.domain.model.tender.criteria

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class CriteriaSource(@JsonValue override val key: String) : EnumElementProvider.Key {

    BUYER("buyer"),
    PROCURING_ENTITY("procuringEntity"),
    TENDERER("tenderer");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriteriaSource>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
