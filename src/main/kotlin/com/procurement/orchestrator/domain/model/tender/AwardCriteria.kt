package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class AwardCriteria(@JsonValue override val key: String) : EnumElementProvider.Key {

    COST_ONLY("costOnly"),
    PRICE_ONLY("priceOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteria>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
