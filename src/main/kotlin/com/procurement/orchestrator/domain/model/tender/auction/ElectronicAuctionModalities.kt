package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class ElectronicAuctionModalities(
    values: List<ElectronicAuctionModality> = emptyList()
) : List<ElectronicAuctionModality> by values,
    ComplexObjects<ElectronicAuctionModality, ElectronicAuctionModalities>,
    Serializable {

    constructor(value: ElectronicAuctionModality) : this(listOf(value))

    override operator fun plus(other: ElectronicAuctionModalities) =
        ElectronicAuctionModalities(this as List<ElectronicAuctionModality> + other as List<ElectronicAuctionModality>)

    override operator fun plus(others: List<ElectronicAuctionModality>) =
        ElectronicAuctionModalities(this as List<ElectronicAuctionModality> + others)

    override fun combineBy(src: ElectronicAuctionModalities) =
        ElectronicAuctionModalities(ComplexObjects.merge(dst = this, src = src))
}
