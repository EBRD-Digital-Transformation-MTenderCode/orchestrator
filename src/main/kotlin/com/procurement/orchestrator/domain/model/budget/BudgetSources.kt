package com.procurement.orchestrator.domain.model.budget

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BudgetSources(values: List<BudgetSource> = emptyList()) : List<BudgetSource> by values,
                                                                IdentifiableObjects<BudgetSource, BudgetSources>,
                                                                Serializable {

    override fun updateBy(src: BudgetSources) = BudgetSources(update(dst = this, src = src))
}
