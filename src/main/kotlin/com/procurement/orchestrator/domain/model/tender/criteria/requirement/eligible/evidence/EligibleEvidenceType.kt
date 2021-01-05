package com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class EligibleEvidenceType(@JsonValue override val key: String) : EnumElementProvider.Key {

    REFERENCE("reference"),
    DOCUMENT("document");

    override fun toString(): String = key

    companion object : EnumElementProvider<EligibleEvidenceType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
