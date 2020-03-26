package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class AwardCriteriaDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    AUTOMATED("automated"),
    MANUAL("manual");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteriaDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
