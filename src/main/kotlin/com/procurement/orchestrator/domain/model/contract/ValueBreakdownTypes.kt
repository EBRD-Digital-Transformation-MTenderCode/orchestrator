package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class ValueBreakdownTypes(
    values: List<ValueBreakdownType> = emptyList()
) : List<ValueBreakdownType> by values, ComplexObjects<ValueBreakdownType, ValueBreakdownTypes> {

    override fun combineBy(src: ValueBreakdownTypes) = ValueBreakdownTypes(merge(dst = this, src = src))
}
