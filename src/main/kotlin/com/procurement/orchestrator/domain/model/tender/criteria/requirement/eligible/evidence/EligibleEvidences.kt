package com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class EligibleEvidences(values: List<EligibleEvidence> = emptyList()) : List<EligibleEvidence> by values,
                                                              IdentifiableObjects<EligibleEvidence, EligibleEvidences>,
                                                              Serializable {

    constructor(value: EligibleEvidence) : this(listOf(value))

    override operator fun plus(other: EligibleEvidences) =
        EligibleEvidences(this as List<EligibleEvidence> + other as List<EligibleEvidence>)

    override operator fun plus(others: List<EligibleEvidence>) = EligibleEvidences(this as List<EligibleEvidence> + others)

    override fun updateBy(src: EligibleEvidences) = EligibleEvidences(update(dst = this, src = src))
}
