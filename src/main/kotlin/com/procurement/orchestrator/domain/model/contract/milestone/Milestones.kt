package com.procurement.orchestrator.domain.model.contract.milestone

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Milestones(values: List<Milestone> = emptyList()) : List<Milestone> by values,
                                                          IdentifiableObjects<Milestone, Milestones>,
                                                          Serializable {

    constructor(value: Milestone) : this(listOf(value))

    override operator fun plus(other: Milestones) = Milestones(this as List<Milestone> + other as List<Milestone>)

    override operator fun plus(others: List<Milestone>) = Milestones(this as List<Milestone> + others)

    override fun updateBy(src: Milestones) = Milestones(update(dst = this, src = src))
}
