package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.ComplexObjects

class ElectronicAuctionModalities(
    values: List<ElectronicAuctionModality> = emptyList()
) : List<ElectronicAuctionModality> by values, ComplexObjects<ElectronicAuctionModality, ElectronicAuctionModalities> {

    override fun combineBy(src: ElectronicAuctionModalities) =
        ElectronicAuctionModalities(ComplexObjects.merge(dst = this, src = src))
}
