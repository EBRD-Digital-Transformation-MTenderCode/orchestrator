package com.procurement.orchestrator.domain.model.item

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Items(values: List<Item> = emptyList()) : List<Item> by values,
                                                IdentifiableObjects<Item, Items>,
                                                Serializable {

    override fun updateBy(src: Items) = Items(update(dst = this, src = src))
}
