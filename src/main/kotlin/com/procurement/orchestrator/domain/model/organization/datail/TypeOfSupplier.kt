package com.procurement.orchestrator.domain.model.organization.datail

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class TypeOfSupplier(@JsonValue override val key: String) : EnumElementProvider.Key {

    COMPANY("company"),
    INDIVIDUAL("individual");

    override fun toString(): String = key

    companion object : EnumElementProvider<TypeOfSupplier>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
