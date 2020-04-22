package com.procurement.orchestrator.domain.model.organization

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Organizations(
    values: List<Organization> = emptyList()
) : List<Organization> by values, IdentifiableObjects<Organization, Organizations> {

    override fun updateBy(src: Organizations) = Organizations(update(dst = this, src = src))
}
