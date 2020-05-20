package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class ProcurementMethodModalities(
    values: List<ProcurementMethodModality> = emptyList()
) : List<ProcurementMethodModality> by values,
    ComplexObjects<ProcurementMethodModality, ProcurementMethodModalities>,
    Serializable {

    constructor(value: ProcurementMethodModality) : this(listOf(value))

    override operator fun plus(other: ProcurementMethodModalities) =
        ProcurementMethodModalities(this as List<ProcurementMethodModality> + other as List<ProcurementMethodModality>)

    override operator fun plus(others: List<ProcurementMethodModality>) =
        ProcurementMethodModalities(this as List<ProcurementMethodModality> + others)

    override fun combineBy(src: ProcurementMethodModalities) =
        ProcurementMethodModalities(ComplexObjects.merge(dst = this, src = src))
}
