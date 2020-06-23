package com.procurement.orchestrator.domain.model.qualification

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Qualifications(values: List<Qualification> = emptyList()) : List<Qualification> by values,
    IdentifiableObjects<Qualification, Qualifications>,
    Serializable {

    constructor(qualification: Qualification) : this(listOf(qualification))

    override operator fun plus(other: Qualifications) =
        Qualifications(this as List<Qualification> + other as List<Qualification>)

    override operator fun plus(others: List<Qualification>) =
        Qualifications(this as List<Qualification> + others)

    override fun updateBy(src: Qualifications) = Qualifications(update(dst = this, src = src))
}
