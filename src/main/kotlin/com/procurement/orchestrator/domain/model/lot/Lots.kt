package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Lots(values: List<Lot> = emptyList()) : List<Lot> by values, IdentifiableObjects<Lot, Lots> {

    constructor(lot: Lot) : this(listOf(lot))

    operator fun plus(other: Lots) = Lots(this as List<Lot> + other as List<Lot>)

    operator fun plus(others: List<Lot>) = Lots(this as List<Lot> + others)

    override fun updateBy(src: Lots) = Lots(update(dst = this, src = src))
}
