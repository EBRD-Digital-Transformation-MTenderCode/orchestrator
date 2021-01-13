package com.procurement.orchestrator.domain.model.requirement.response.evidence

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Evidences(values: List<Evidence> = emptyList()) : List<Evidence> by values,
                                                              IdentifiableObjects<Evidence, Evidences>,
                                                              Serializable {

    constructor(value: Evidence) : this(listOf(value))

    override operator fun plus(other: Evidences) =
        Evidences(this as List<Evidence> + other as List<Evidence>)

    override operator fun plus(others: List<Evidence>) = Evidences(this as List<Evidence> + others)

    override fun updateBy(src: Evidences) = Evidences(update(dst = this, src = src))
}
