package com.procurement.orchestrator.domain.model.organization.datail

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class MainEconomicActivities(
    values: List<MainEconomicActivity> = emptyList()
) : List<MainEconomicActivity> by values,
    ComplexObjects<MainEconomicActivity, MainEconomicActivities>,
    Serializable {

    override fun combineBy(src: MainEconomicActivities) = MainEconomicActivities(merge(dst = this, src = src))
}
