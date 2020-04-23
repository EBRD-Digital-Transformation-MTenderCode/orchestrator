package com.procurement.orchestrator.domain.model.organization.datail.permit

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Permits(values: List<Permit> = emptyList()) : List<Permit> by values,
                                                    IdentifiableObjects<Permit, Permits>,
                                                    Serializable {

    override fun updateBy(src: Permits) = Permits(update(dst = this, src = src))
}
