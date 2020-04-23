package com.procurement.orchestrator.domain.model.bid

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BidsDetails(values: List<Bid> = emptyList()) : List<Bid> by values,
                                                     IdentifiableObjects<Bid, BidsDetails>,
                                                     Serializable {

    constructor(value: Bid) : this(listOf(value))

    override operator fun plus(other: BidsDetails) = BidsDetails(this as List<Bid> + other as List<Bid>)

    override operator fun plus(others: List<Bid>) = BidsDetails(this as List<Bid> + others)

    override fun updateBy(src: BidsDetails) = BidsDetails(update(dst = this, src = src))
}
