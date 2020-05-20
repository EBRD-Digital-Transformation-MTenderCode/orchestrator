package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus

enum class AwardStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    AWAITING("awaiting"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    LOT_CANCELLED("lotCancelled");

    override fun toString(): String = key

    companion object : EnumElementProvider<AmendmentStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AmendmentStatus.orThrow(name)
    }
}
