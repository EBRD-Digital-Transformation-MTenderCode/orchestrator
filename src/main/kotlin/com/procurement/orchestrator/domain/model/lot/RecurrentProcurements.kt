package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class RecurrentProcurements(
    values: List<RecurrentProcurement> = emptyList()
) : List<RecurrentProcurement> by values,
    ComplexObjects<RecurrentProcurement, RecurrentProcurements>,
    Serializable {

    constructor(value: RecurrentProcurement) : this(listOf(value))

    override operator fun plus(other: RecurrentProcurements) =
        RecurrentProcurements(this as List<RecurrentProcurement> + other as List<RecurrentProcurement>)

    override operator fun plus(others: List<RecurrentProcurement>) =
        RecurrentProcurements(this as List<RecurrentProcurement> + others)

    override fun combineBy(src: RecurrentProcurements) =
        RecurrentProcurements(ComplexObjects.merge(dst = this, src = src))
}
