package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects

class Renewals(values: List<Renewal> = emptyList()) : List<Renewal> by values, ComplexObjects<Renewal, Renewals> {

    override fun combineBy(src: Renewals) = Renewals(ComplexObjects.merge(dst = this, src = src))
}
