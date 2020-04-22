package com.procurement.orchestrator.domain.model.organization.datail.permit

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Permits(values: List<Permit> = emptyList()) : List<Permit> by values, IdentifiableObjects<Permit, Permits> {

    override fun updateBy(src: Permits) = Permits(update(dst = this, src = src))
}
