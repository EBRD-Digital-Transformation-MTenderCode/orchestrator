package com.procurement.orchestrator.domain.model.party

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class PartyRoles(values: List<PartyRole> = emptyList()) : List<PartyRole> by values,
                                                          ComplexObjects<PartyRole, PartyRoles>,
                                                          Serializable {

    override fun combineBy(src: PartyRoles) = PartyRoles(merge(dst = this, src = src))
}
