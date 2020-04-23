package com.procurement.orchestrator.domain.model.organization.person

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class BusinessFunctions(
    values: List<BusinessFunction> = emptyList()
) : List<BusinessFunction> by values, IdentifiableObjects<BusinessFunction, BusinessFunctions> {

    override fun updateBy(src: BusinessFunctions) = BusinessFunctions(update(dst = this, src = src))
}
