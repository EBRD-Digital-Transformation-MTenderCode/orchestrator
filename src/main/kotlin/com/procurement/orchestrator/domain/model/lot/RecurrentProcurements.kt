package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects

class RecurrentProcurements(
    values: List<RecurrentProcurement> = emptyList()
) : List<RecurrentProcurement> by values, ComplexObjects<RecurrentProcurement, RecurrentProcurements> {

    override fun combineBy(src: RecurrentProcurements) =
        RecurrentProcurements(ComplexObjects.merge(dst = this, src = src))
}
