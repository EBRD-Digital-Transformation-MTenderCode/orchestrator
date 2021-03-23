package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus

enum class AwardStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    ACTIVE("active"),
    AWAITING("awaiting"),
    BASED_ON_HUMAN_DECISION("basedOnHumanDecision"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    LACK_OF_QUALIFICATIONS("lackOfQualifications"),
    LACK_OF_SUBMISSIONS("lackOfSubmissions"),
    LOT_CANCELLED("lotCancelled"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    PENDING("pending"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AmendmentStatus.orThrow(name)
    }
}
