package com.procurement.orchestrator.domain.model.organization.person

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BusinessFunctions(
    values: List<BusinessFunction> = emptyList()
) : List<BusinessFunction> by values,
    IdentifiableObjects<BusinessFunction, BusinessFunctions>,
    Serializable {

    override fun updateBy(src: BusinessFunctions) = BusinessFunctions(update(dst = this, src = src))
}
