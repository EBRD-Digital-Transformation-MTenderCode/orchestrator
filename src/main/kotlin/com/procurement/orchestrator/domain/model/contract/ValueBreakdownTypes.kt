package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class ValueBreakdownTypes(
    values: List<ValueBreakdownType> = emptyList()
) : List<ValueBreakdownType> by values,
    ComplexObjects<ValueBreakdownType, ValueBreakdownTypes>,
    Serializable {

    @JsonCreator
    constructor(vararg values: ValueBreakdownType) : this(values.toList())

    override operator fun plus(other: ValueBreakdownTypes) =
        ValueBreakdownTypes(this as List<ValueBreakdownType> + other as List<ValueBreakdownType>)

    override operator fun plus(others: List<ValueBreakdownType>) =
        ValueBreakdownTypes(this as List<ValueBreakdownType> + others)

    override fun combineBy(src: ValueBreakdownTypes) = ValueBreakdownTypes(merge(dst = this, src = src))
}
