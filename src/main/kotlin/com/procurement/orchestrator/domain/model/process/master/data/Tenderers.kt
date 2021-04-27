package com.procurement.orchestrator.domain.model.process.master.data

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class Tenderers(values: List<Tenderer> = emptyList()) : List<Tenderer> by values,
    ComplexObjects<Tenderer, Tenderers>,
    Serializable {

    constructor(value: Tenderer) : this(listOf(value))

    override operator fun plus(other: Tenderers) = Tenderers(this as List<Tenderer> + other as List<Tenderer>)

    override operator fun plus(others: List<Tenderer>) = Tenderers(this as List<Tenderer> + others)

    override fun combineBy(src: Tenderers) = Tenderers(ComplexObjects.merge(dst = this, src = src))
}
