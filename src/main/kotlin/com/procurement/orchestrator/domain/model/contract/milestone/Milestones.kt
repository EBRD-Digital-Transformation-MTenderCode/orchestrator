package com.procurement.orchestrator.domain.model.contract.milestone

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Milestones(values: List<Milestone> = emptyList()) : List<Milestone> by values,
    IdentifiableObjects<Milestone, Milestones> {

    override fun updateBy(src: Milestones) = Milestones(update(dst = this, src = src))
}
