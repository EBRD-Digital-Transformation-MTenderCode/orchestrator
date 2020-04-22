package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class RelatedLots(values: List<LotId> = emptyList()) : List<LotId> by values, ComplexObjects<LotId, RelatedLots> {

    override fun combineBy(src: RelatedLots) = RelatedLots(merge(dst = this, src = src))
}
