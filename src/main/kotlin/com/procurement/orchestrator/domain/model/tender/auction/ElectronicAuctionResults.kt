package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class ElectronicAuctionResults(values: List<ElectronicAuctionResult> = emptyList()) :
    List<ElectronicAuctionResult> by values, IdentifiableObjects<ElectronicAuctionResult, ElectronicAuctionResults> {

    override fun updateBy(src: ElectronicAuctionResults) = ElectronicAuctionResults(update(dst = this, src = src))
}
