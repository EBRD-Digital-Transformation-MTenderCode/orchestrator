package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ValueBreakdowns(
    values: List<ValueBreakdown> = emptyList()
) : List<ValueBreakdown> by values,
    IdentifiableObjects<ValueBreakdown, ValueBreakdowns>,
    Serializable {

    override fun updateBy(src: ValueBreakdowns) = ValueBreakdowns(update(dst = this, src = src))
}
