package com.procurement.orchestrator.domain.model.organization

import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import com.procurement.orchestrator.domain.model.IdentifiableObjects
import java.io.Serializable

class OrganizationReferences(
    values: List<OrganizationReference> = emptyList()
) : List<OrganizationReference> by values,
    IdentifiableObjects<OrganizationReference, OrganizationReferences>,
    Serializable {

    override fun updateBy(src: OrganizationReferences) = OrganizationReferences(merge(dst = this, src = src))
}
