package com.procurement.orchestrator.domain.model.item

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class RelatedItems(values: List<ItemId> = emptyList()) : List<ItemId> by values,
                                                         ComplexObjects<ItemId, RelatedItems>,
                                                         Serializable {

    override fun combineBy(src: RelatedItems) = RelatedItems(merge(dst = this, src = src))
}
