package com.procurement.orchestrator.domain.model.item

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class RelatedItems(values: List<ItemId> = emptyList()) : List<ItemId> by values,
                                                         ComplexObjects<ItemId, RelatedItems>,
                                                         Serializable {

    constructor(value: ItemId) : this(listOf(value))

    override operator fun plus(other: RelatedItems) = RelatedItems(this as List<ItemId> + other as List<ItemId>)

    override operator fun plus(others: List<ItemId>) = RelatedItems(this as List<ItemId> + others)

    override fun combineBy(src: RelatedItems) = RelatedItems(merge(dst = this, src = src))
}
