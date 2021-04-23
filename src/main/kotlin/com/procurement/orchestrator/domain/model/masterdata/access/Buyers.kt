package com.procurement.orchestrator.domain.model.masterdata.access

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Buyers(
    values: List<Buyer> = emptyList()
) : List<Buyer> by values,
    IdentifiableObjects<Buyer, Buyers>,
    Serializable {

    constructor(value: Buyer) : this(listOf(value))

    override operator fun plus(other: Buyers) =
        Buyers(this as List<Buyer> + other as List<Buyer>)

    override operator fun plus(others: List<Buyer>) = Buyers(this as List<Buyer> + others)

    override fun updateBy(src: Buyers) = Buyers(update(dst = this, src = src))
}
