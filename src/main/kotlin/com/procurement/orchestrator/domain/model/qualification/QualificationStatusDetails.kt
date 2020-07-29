package com.procurement.orchestrator.domain.model.qualification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class QualificationStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    ACTIVE("active"),
    AWAITING("awaiting"),
    BASED_ON_HUMAN_DECISION("basedOnHumanDecision"),
    CONSIDERATION("consideration"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<QualificationStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
