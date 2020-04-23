package com.procurement.orchestrator.domain.model.organization

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Organizations(
    values: List<Organization> = emptyList()
) : List<Organization> by values,
    IdentifiableObjects<Organization, Organizations>,
    Serializable {

    override fun updateBy(src: Organizations) = Organizations(update(dst = this, src = src))
}
