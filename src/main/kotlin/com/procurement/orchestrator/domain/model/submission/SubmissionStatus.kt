package com.procurement.orchestrator.domain.model.submission

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus

enum class SubmissionStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    VALID("valid"),
    WITHDRAWN("withdrawn"),
    DISQUALIFIED("disqualified");

    override fun toString(): String = key

    companion object : EnumElementProvider<SubmissionStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AmendmentStatus.orThrow(name)
    }
}
