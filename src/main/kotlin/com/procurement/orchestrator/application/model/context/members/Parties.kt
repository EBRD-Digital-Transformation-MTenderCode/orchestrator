package com.procurement.orchestrator.application.model.context.members

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.party.Party
import java.io.Serializable

class Parties(values: List<Party> = emptyList()) : List<Party> by values,
                                                   IdentifiableObjects<Party, Parties>,
                                                   Serializable {

    constructor(party: Party) : this(listOf(party))

    override operator fun plus(other: Parties): Parties = Parties(this as List<Party> + other as List<Party>)

    override operator fun plus(others: List<Party>): Parties = Parties(this as List<Party> + others)

    override fun updateBy(src: Parties) = Parties(ComplexObjects.merge(dst = this, src = src))
}
