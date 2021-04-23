package com.procurement.orchestrator.domain.model.mdm

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import java.io.Serializable

class Organizations(values: List<Organization> = emptyList()) : List<Organization> by values,
    IdentifiableObjects<Organization, Organizations>,
    Serializable {

    constructor(lot: Organization) : this(listOf(lot))

    override operator fun plus(other: Organizations) = Organizations(this as List<Organization> + other as List<Organization>)

    override operator fun plus(others: List<Organization>) = Organizations(this as List<Organization> + others)

    override fun updateBy(src: Organizations) = Organizations(IdentifiableObjects.update(dst = this, src = src))
}
