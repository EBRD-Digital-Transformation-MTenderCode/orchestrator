package com.procurement.orchestrator.domain.model.organization.datail.permit

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Permits(values: List<Permit> = emptyList()) : List<Permit> by values,
                                                    IdentifiableObjects<Permit, Permits>,
                                                    Serializable {

    constructor(value: Permit) : this(listOf(value))

    override operator fun plus(other: Permits) = Permits(this as List<Permit> + other as List<Permit>)

    override operator fun plus(others: List<Permit>) = Permits(this as List<Permit> + others)

    override fun updateBy(src: Permits) = Permits(update(dst = this, src = src))
}
