package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ValueBreakdowns(
    values: List<ValueBreakdown> = emptyList()
) : List<ValueBreakdown> by values,
    IdentifiableObjects<ValueBreakdown, ValueBreakdowns>,
    Serializable {

    constructor(value: ValueBreakdown) : this(listOf(value))

    override operator fun plus(other: ValueBreakdowns) =
        ValueBreakdowns(this as List<ValueBreakdown> + other as List<ValueBreakdown>)

    override operator fun plus(others: List<ValueBreakdown>) = ValueBreakdowns(this as List<ValueBreakdown> + others)

    override fun updateBy(src: ValueBreakdowns) = ValueBreakdowns(update(dst = this, src = src))
}
