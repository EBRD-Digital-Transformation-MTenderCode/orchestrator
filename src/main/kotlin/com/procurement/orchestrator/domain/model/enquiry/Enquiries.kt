package com.procurement.orchestrator.domain.model.enquiry

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Enquiries(values: List<Enquiry> = emptyList()) : List<Enquiry> by values,
                                                       IdentifiableObjects<Enquiry, Enquiries>,
                                                       Serializable {

    constructor(value: Enquiry) : this(listOf(value))

    override operator fun plus(other: Enquiries) = Enquiries(this as List<Enquiry> + other as List<Enquiry>)

    override operator fun plus(others: List<Enquiry>) = Enquiries(this as List<Enquiry> + others)

    override fun updateBy(src: Enquiries) = Enquiries(update(dst = this, src = src))
}
