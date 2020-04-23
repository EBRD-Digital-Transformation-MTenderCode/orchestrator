package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class LotVariants(
    values: List<LotVariant> = emptyList()
) : List<LotVariant> by values, ComplexObjects<LotVariant, LotVariants> {

    override fun combineBy(src: LotVariants) = LotVariants(merge(dst = this, src = src))
}
