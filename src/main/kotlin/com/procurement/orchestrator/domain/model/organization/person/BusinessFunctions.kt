package com.procurement.orchestrator.domain.model.organization.person

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BusinessFunctions(
    values: List<BusinessFunction> = emptyList()
) : List<BusinessFunction> by values,
    IdentifiableObjects<BusinessFunction, BusinessFunctions>,
    Serializable {

    constructor(value: BusinessFunction) : this(listOf(value))

    override operator fun plus(other: BusinessFunctions) =
        BusinessFunctions(this as List<BusinessFunction> + other as List<BusinessFunction>)

    override operator fun plus(others: List<BusinessFunction>) =
        BusinessFunctions(this as List<BusinessFunction> + others)

    override fun updateBy(src: BusinessFunctions) = BusinessFunctions(update(dst = this, src = src))
}
