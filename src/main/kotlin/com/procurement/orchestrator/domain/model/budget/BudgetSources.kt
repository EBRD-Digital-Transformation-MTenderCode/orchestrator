package com.procurement.orchestrator.domain.model.budget

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BudgetSources(values: List<BudgetSource> = emptyList()) : List<BudgetSource> by values,
                                                                IdentifiableObjects<BudgetSource, BudgetSources>,
                                                                Serializable {

    constructor(value: BudgetSource) : this(listOf(value))

    override operator fun plus(other: BudgetSources) =
        BudgetSources(this as List<BudgetSource> + other as List<BudgetSource>)

    override operator fun plus(others: List<BudgetSource>) = BudgetSources(this as List<BudgetSource> + others)

    override fun updateBy(src: BudgetSources) = BudgetSources(update(dst = this, src = src))
}
