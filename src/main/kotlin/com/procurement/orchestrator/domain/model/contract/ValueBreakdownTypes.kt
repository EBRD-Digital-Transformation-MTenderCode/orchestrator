package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class ValueBreakdownTypes(
    values: List<ValueBreakdownType> = emptyList()
) : List<ValueBreakdownType> by values,
    ComplexObjects<ValueBreakdownType, ValueBreakdownTypes>,
    Serializable {

    constructor(value: ValueBreakdownType) : this(listOf(value))

    override operator fun plus(other: ValueBreakdownTypes) =
        ValueBreakdownTypes(this as List<ValueBreakdownType> + other as List<ValueBreakdownType>)

    override operator fun plus(others: List<ValueBreakdownType>) =
        ValueBreakdownTypes(this as List<ValueBreakdownType> + others)

    override fun combineBy(src: ValueBreakdownTypes) = ValueBreakdownTypes(merge(dst = this, src = src))
}
