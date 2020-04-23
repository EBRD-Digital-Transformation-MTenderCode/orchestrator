package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Requirements(values: List<Requirement> = emptyList()) : List<Requirement> by values,
                                                              IdentifiableObjects<Requirement, Requirements>,
                                                              Serializable {

    constructor(value: Requirement) : this(listOf(value))

    override operator fun plus(other: Requirements) =
        Requirements(this as List<Requirement> + other as List<Requirement>)

    override operator fun plus(others: List<Requirement>) = Requirements(this as List<Requirement> + others)

    override fun updateBy(src: Requirements) = Requirements(update(dst = this, src = src))
}
