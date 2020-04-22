package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class ElectronicAuctionProgressBreakdowns(
    values: List<ElectronicAuctionProgressBreakdown> = emptyList()
) : List<ElectronicAuctionProgressBreakdown> by values,
    IdentifiableObjects<ElectronicAuctionProgressBreakdown, ElectronicAuctionProgressBreakdowns> {

    override fun updateBy(src: ElectronicAuctionProgressBreakdowns) =
        ElectronicAuctionProgressBreakdowns(update(dst = this, src = src))
}
