package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class ValueBreakdownTypes(
    values: List<ValueBreakdownType> = emptyList()
) : List<ValueBreakdownType> by values,
    ComplexObjects<ValueBreakdownType, ValueBreakdownTypes>,
    Serializable {

    override fun combineBy(src: ValueBreakdownTypes) = ValueBreakdownTypes(merge(dst = this, src = src))
}
