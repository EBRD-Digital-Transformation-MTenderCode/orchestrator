package com.procurement.orchestrator.domain.model.tender.criteria

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Criterias(values: List<Criteria> = emptyList()) : List<Criteria> by values,
    IdentifiableObjects<Criteria, Criterias> {

    override fun updateBy(src: Criterias) = Criterias(update(dst = this, src = src))
}
