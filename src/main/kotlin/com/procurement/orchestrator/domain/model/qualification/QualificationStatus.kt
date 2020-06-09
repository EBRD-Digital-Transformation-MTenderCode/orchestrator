package com.procurement.orchestrator.domain.model.qualification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class QualificationStatus(@JsonValue override val key: String) : EnumElementProvider.Key {

    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<QualificationStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
