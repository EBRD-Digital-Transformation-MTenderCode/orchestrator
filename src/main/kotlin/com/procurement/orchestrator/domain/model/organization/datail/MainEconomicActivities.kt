package com.procurement.orchestrator.domain.model.organization.datail

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class MainEconomicActivities(
    values: List<MainEconomicActivity> = emptyList()
) : List<MainEconomicActivity> by values,
    ComplexObjects<MainEconomicActivity, MainEconomicActivities>,
    Serializable {

    constructor(value: MainEconomicActivity) : this(listOf(value))

    override operator fun plus(other: MainEconomicActivities) =
        MainEconomicActivities(this as List<MainEconomicActivity> + other as List<MainEconomicActivity>)

    override operator fun plus(others: List<MainEconomicActivity>) =
        MainEconomicActivities(this as List<MainEconomicActivity> + others)

    override fun combineBy(src: MainEconomicActivities) = MainEconomicActivities(merge(dst = this, src = src))
}
