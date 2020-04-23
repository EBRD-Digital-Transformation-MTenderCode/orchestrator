package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Requirements(values: List<Requirement> = emptyList()) : List<Requirement> by values,
                                                              IdentifiableObjects<Requirement, Requirements>,
                                                              Serializable {

    override fun updateBy(src: Requirements) = Requirements(update(dst = this, src = src))
}
