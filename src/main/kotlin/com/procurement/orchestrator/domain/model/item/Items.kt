package com.procurement.orchestrator.domain.model.item

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Items(values: List<Item> = emptyList()) : List<Item> by values, IdentifiableObjects<Item, Items> {

    override fun updateBy(src: Items) = Items(update(dst = this, src = src))
}
