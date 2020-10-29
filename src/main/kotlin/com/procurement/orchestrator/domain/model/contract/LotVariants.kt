package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class LotVariants(values: List<LotVariant> = emptyList()) : List<LotVariant> by values,
    ComplexObjects<LotVariant, LotVariants>,
    Serializable {

    @JsonCreator
    constructor(vararg values: LotVariant) : this(values.toList())

    override operator fun plus(other: LotVariants) = LotVariants(this as List<LotVariant> + other as List<LotVariant>)

    override operator fun plus(others: List<LotVariant>) = LotVariants(this as List<LotVariant> + others)

    override fun combineBy(src: LotVariants) = LotVariants(merge(dst = this, src = src))
}
