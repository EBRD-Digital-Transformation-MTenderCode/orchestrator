package com.procurement.orchestrator.domain.model.bid

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class BidStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    ARCHIVED("archived"),
    DISQUALIFIED("disqualified"),
    EMPTY("empty"),
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    WITHDRAWN("withdrawn");

    override fun toString(): String = key

    companion object : EnumElementProvider<BidStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
