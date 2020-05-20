package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class LotVariants(values: List<LotVariant> = emptyList()) : List<LotVariant> by values,
                                                            ComplexObjects<LotVariant, LotVariants>,
                                                            Serializable {

    constructor(value: LotVariant) : this(listOf(value))

    override operator fun plus(other: LotVariants) = LotVariants(this as List<LotVariant> + other as List<LotVariant>)

    override operator fun plus(others: List<LotVariant>) = LotVariants(this as List<LotVariant> + others)

    override fun combineBy(src: LotVariants) = LotVariants(merge(dst = this, src = src))
}
