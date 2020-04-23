package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ElectronicAuctionProgresses(
    values: List<ElectronicAuctionProgress> = emptyList()
) : List<ElectronicAuctionProgress> by values,
    IdentifiableObjects<ElectronicAuctionProgress, ElectronicAuctionProgresses>,
    Serializable {

    override fun updateBy(src: ElectronicAuctionProgresses) = ElectronicAuctionProgresses(update(dst = this, src = src))
}
