package com.procurement.orchestrator.domain.model.candidate

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import com.procurement.orchestrator.domain.model.organization.Organization
import java.io.Serializable

class Candidates(
    values: List<Organization> = emptyList()
) : List<Organization> by values,
    IdentifiableObjects<Organization, Candidates>,
    Serializable {

    constructor(value: Organization) : this(listOf(value))

    override operator fun plus(other: Candidates) =
        Candidates(this as List<Organization> + other as List<Organization>)

    override operator fun plus(others: List<Organization>) =
        Candidates(this as List<Organization> + others)

    override fun updateBy(src: Candidates) = Candidates(update(dst = this, src = src))
}
