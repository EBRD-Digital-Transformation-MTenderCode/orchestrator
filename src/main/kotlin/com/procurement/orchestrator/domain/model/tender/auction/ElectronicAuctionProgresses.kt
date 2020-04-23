package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ElectronicAuctionProgresses(
    values: List<ElectronicAuctionProgress> = emptyList()
) : List<ElectronicAuctionProgress> by values,
    IdentifiableObjects<ElectronicAuctionProgress, ElectronicAuctionProgresses>,
    Serializable {

    constructor(value: ElectronicAuctionProgress) : this(listOf(value))

    override operator fun plus(other: ElectronicAuctionProgresses) =
        ElectronicAuctionProgresses(this as List<ElectronicAuctionProgress> + other as List<ElectronicAuctionProgress>)

    override operator fun plus(others: List<ElectronicAuctionProgress>) =
        ElectronicAuctionProgresses(this as List<ElectronicAuctionProgress> + others)

    override fun updateBy(src: ElectronicAuctionProgresses) = ElectronicAuctionProgresses(update(dst = this, src = src))
}
