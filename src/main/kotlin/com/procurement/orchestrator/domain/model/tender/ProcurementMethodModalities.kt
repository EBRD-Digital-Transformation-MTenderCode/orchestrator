package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class ProcurementMethodModalities(
    values: List<ProcurementMethodModality> = emptyList()
) : List<ProcurementMethodModality> by values,
    ComplexObjects<ProcurementMethodModality, ProcurementMethodModalities>,
    Serializable {

    @JsonCreator
    constructor(vararg values: ProcurementMethodModality) : this(values.toList())

    override operator fun plus(other: ProcurementMethodModalities) =
        ProcurementMethodModalities(this as List<ProcurementMethodModality> + other as List<ProcurementMethodModality>)

    override operator fun plus(others: List<ProcurementMethodModality>) =
        ProcurementMethodModalities(this as List<ProcurementMethodModality> + others)

    override fun combineBy(src: ProcurementMethodModalities) = ProcurementMethodModalities(merge(dst = this, src = src))
}
