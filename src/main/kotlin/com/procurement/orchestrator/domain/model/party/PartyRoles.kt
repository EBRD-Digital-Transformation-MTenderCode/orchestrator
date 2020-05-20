package com.procurement.orchestrator.domain.model.party

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class PartyRoles(values: List<PartyRole> = emptyList()) : List<PartyRole> by values,
                                                          ComplexObjects<PartyRole, PartyRoles>,
                                                          Serializable {

    constructor(value: PartyRole) : this(listOf(value))

    override operator fun plus(other: PartyRoles) = PartyRoles(this as List<PartyRole> + other as List<PartyRole>)

    override operator fun plus(others: List<PartyRole>) = PartyRoles(this as List<PartyRole> + others)

    override fun combineBy(src: PartyRoles) = PartyRoles(merge(dst = this, src = src))
}
