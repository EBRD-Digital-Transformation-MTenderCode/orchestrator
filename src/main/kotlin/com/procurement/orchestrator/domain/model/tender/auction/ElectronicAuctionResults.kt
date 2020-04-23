package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ElectronicAuctionResults(
    values: List<ElectronicAuctionResult> = emptyList()
) : List<ElectronicAuctionResult> by values,
    IdentifiableObjects<ElectronicAuctionResult, ElectronicAuctionResults>,
    Serializable {

    constructor(value: ElectronicAuctionResult) : this(listOf(value))

    override operator fun plus(other: ElectronicAuctionResults) =
        ElectronicAuctionResults(this as List<ElectronicAuctionResult> + other as List<ElectronicAuctionResult>)

    override operator fun plus(others: List<ElectronicAuctionResult>) =
        ElectronicAuctionResults(this as List<ElectronicAuctionResult> + others)

    override fun updateBy(src: ElectronicAuctionResults) = ElectronicAuctionResults(update(dst = this, src = src))
}
