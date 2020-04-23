package com.procurement.orchestrator.domain.model.tender.criteria

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Criterias(values: List<Criteria> = emptyList()) : List<Criteria> by values,
                                                        IdentifiableObjects<Criteria, Criterias>,
                                                        Serializable {

    constructor(value: Criteria) : this(listOf(value))

    override operator fun plus(other: Criterias) = Criterias(this as List<Criteria> + other as List<Criteria>)

    override operator fun plus(others: List<Criteria>) = Criterias(this as List<Criteria> + others)

    override fun updateBy(src: Criterias) = Criterias(update(dst = this, src = src))
}
