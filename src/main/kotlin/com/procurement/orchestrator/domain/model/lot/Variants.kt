package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class Variants(values: List<Variant> = emptyList()) : List<Variant> by values,
    ComplexObjects<Variant, Variants>,
    Serializable {

    constructor(value: Variant) : this(listOf(value))

    override operator fun plus(other: Variants) = Variants(this as List<Variant> + other as List<Variant>)

    override operator fun plus(others: List<Variant>) = Variants(this as List<Variant> + others)

    override fun combineBy(src: Variants) = Variants(ComplexObjects.merge(dst = this, src = src))
}