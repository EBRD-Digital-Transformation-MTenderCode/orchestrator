package com.procurement.orchestrator.domain.model.organization.datail

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class MainEconomicActivities(
    values: List<MainEconomicActivity> = emptyList()
) : List<MainEconomicActivity> by values, ComplexObjects<MainEconomicActivity, MainEconomicActivities> {

    override fun combineBy(src: MainEconomicActivities) = MainEconomicActivities(merge(dst = this, src = src))
}
