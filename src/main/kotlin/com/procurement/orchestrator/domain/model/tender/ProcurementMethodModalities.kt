package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class ProcurementMethodModalities(
    values: List<ProcurementMethodModality> = emptyList()
) : List<ProcurementMethodModality> by values,
    ComplexObjects<ProcurementMethodModality, ProcurementMethodModalities>,
    Serializable {

    override fun combineBy(src: ProcurementMethodModalities) =
        ProcurementMethodModalities(ComplexObjects.merge(dst = this, src = src))
}
