package com.procurement.orchestrator.domain.model.enquiry

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Enquiries(
    values: List<Enquiry> = emptyList()
) : List<Enquiry> by values, IdentifiableObjects<Enquiry, Enquiries> {

    override fun updateBy(src: Enquiries) = Enquiries(update(dst = this, src = src))
}
