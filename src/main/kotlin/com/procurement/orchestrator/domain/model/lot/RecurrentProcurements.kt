package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class RecurrentProcurements(
    values: List<RecurrentProcurement> = emptyList()
) : List<RecurrentProcurement> by values,
    ComplexObjects<RecurrentProcurement, RecurrentProcurements>,
    Serializable {

    override fun combineBy(src: RecurrentProcurements) =
        RecurrentProcurements(ComplexObjects.merge(dst = this, src = src))
}
