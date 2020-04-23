package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ElectronicAuctionProgressBreakdowns(
    values: List<ElectronicAuctionProgressBreakdown> = emptyList()
) : List<ElectronicAuctionProgressBreakdown> by values,
    IdentifiableObjects<ElectronicAuctionProgressBreakdown, ElectronicAuctionProgressBreakdowns>,
    Serializable {

    constructor(value: ElectronicAuctionProgressBreakdown) : this(listOf(value))

    override operator fun plus(other: ElectronicAuctionProgressBreakdowns) =
        ElectronicAuctionProgressBreakdowns(this as List<ElectronicAuctionProgressBreakdown> + other as List<ElectronicAuctionProgressBreakdown>)

    override operator fun plus(others: List<ElectronicAuctionProgressBreakdown>) =
        ElectronicAuctionProgressBreakdowns(this as List<ElectronicAuctionProgressBreakdown> + others)

    override fun updateBy(src: ElectronicAuctionProgressBreakdowns) =
        ElectronicAuctionProgressBreakdowns(update(dst = this, src = src))
}
