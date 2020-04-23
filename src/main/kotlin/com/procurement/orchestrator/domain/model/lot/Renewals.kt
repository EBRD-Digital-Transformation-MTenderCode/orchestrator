package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class Renewals(values: List<Renewal> = emptyList()) : List<Renewal> by values,
                                                      ComplexObjects<Renewal, Renewals>,
                                                      Serializable {

    constructor(value: Renewal) : this(listOf(value))

    override operator fun plus(other: Renewals) = Renewals(this as List<Renewal> + other as List<Renewal>)

    override operator fun plus(others: List<Renewal>) = Renewals(this as List<Renewal> + others)

    override fun combineBy(src: Renewals) = Renewals(ComplexObjects.merge(dst = this, src = src))
}
