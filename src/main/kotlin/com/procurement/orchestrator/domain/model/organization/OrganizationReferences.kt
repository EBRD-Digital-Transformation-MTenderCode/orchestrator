package com.procurement.orchestrator.domain.model.organization

import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import com.procurement.orchestrator.domain.model.IdentifiableObjects

class OrganizationReferences(
    values: List<OrganizationReference> = emptyList()
) : List<OrganizationReference> by values, IdentifiableObjects<OrganizationReference, OrganizationReferences> {

    override fun updateBy(src: OrganizationReferences) = OrganizationReferences(merge(dst = this, src = src))
}
