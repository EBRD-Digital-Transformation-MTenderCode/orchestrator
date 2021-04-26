package com.procurement.orchestrator.domain.model.process.master.data

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import java.io.Serializable

class Buyers(values: List<Buyer> = emptyList()) : List<Buyer> by values,
    IdentifiableObjects<Buyer, Buyers>,
    Serializable {

    constructor(lot: Buyer) : this(listOf(lot))

    override operator fun plus(other: Buyers) = Buyers(this as List<Buyer> + other as List<Buyer>)

    override operator fun plus(others: List<Buyer>) = Buyers(this as List<Buyer> + others)

    override fun updateBy(src: Buyers) = Buyers(IdentifiableObjects.update(dst = this, src = src))
}
