package com.procurement.orchestrator.domain.model.tender.criteria

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Criterias(values: List<Criterion> = emptyList()) : List<Criterion> by values,
                                                         IdentifiableObjects<Criterion, Criterias>,
                                                         Serializable {

    constructor(value: Criterion) : this(listOf(value))

    override operator fun plus(other: Criterias) = Criterias(this as List<Criterion> + other as List<Criterion>)

    override operator fun plus(others: List<Criterion>) = Criterias(this as List<Criterion> + others)

    override fun updateBy(src: Criterias) = Criterias(update(dst = this, src = src))
}
