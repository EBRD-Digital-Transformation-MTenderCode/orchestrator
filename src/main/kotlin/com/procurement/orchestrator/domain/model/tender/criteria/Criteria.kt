package com.procurement.orchestrator.domain.model.tender.criteria

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Criteria(values: List<Criterion> = emptyList()) : List<Criterion> by values,
                                                        IdentifiableObjects<Criterion, Criteria>,
                                                        Serializable {

    constructor(value: Criterion) : this(listOf(value))

    override operator fun plus(other: Criteria) = Criteria(this as List<Criterion> + other as List<Criterion>)

    override operator fun plus(others: List<Criterion>) = Criteria(this as List<Criterion> + others)

    override fun updateBy(src: Criteria) = Criteria(update(dst = this, src = src))
}
