package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class RelatedLots(values: List<LotId> = emptyList()) : List<LotId> by values,
                                                       ComplexObjects<LotId, RelatedLots>,
                                                       Serializable {

    constructor(value: LotId) : this(listOf(value))

    override operator fun plus(other: RelatedLots) = RelatedLots(this as List<LotId> + other as List<LotId>)

    override operator fun plus(others: List<LotId>) = RelatedLots(this as List<LotId> + others)

    override fun combineBy(src: RelatedLots) = RelatedLots(merge(dst = this, src = src))
}
