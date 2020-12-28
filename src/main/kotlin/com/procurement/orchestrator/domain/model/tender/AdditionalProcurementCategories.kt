package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable


class AdditionalProcurementCategories(
    values: List<ProcurementCategory> = emptyList()
) : List<ProcurementCategory> by values,
    ComplexObjects<ProcurementCategory, AdditionalProcurementCategories>,
    Serializable {

    constructor(value: ProcurementCategory) : this(listOf(value))

    override operator fun plus(other: AdditionalProcurementCategories) =
        AdditionalProcurementCategories(this as List<ProcurementCategory> + other as List<ProcurementCategory>)

    override operator fun plus(others: List<ProcurementCategory>) =
        AdditionalProcurementCategories(this as List<ProcurementCategory> + others)

    override fun combineBy(src: AdditionalProcurementCategories) = AdditionalProcurementCategories(ComplexObjects.merge(dst = this, src = src))
}
