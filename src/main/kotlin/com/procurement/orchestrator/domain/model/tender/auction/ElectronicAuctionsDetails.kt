package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ElectronicAuctionsDetails(
    values: List<ElectronicAuctionsDetail> = emptyList()
) : List<ElectronicAuctionsDetail> by values,
    IdentifiableObjects<ElectronicAuctionsDetail, ElectronicAuctionsDetails>,
    Serializable {

    constructor(value: ElectronicAuctionsDetail) : this(listOf(value))

    override operator fun plus(other: ElectronicAuctionsDetails) =
        ElectronicAuctionsDetails(this as List<ElectronicAuctionsDetail> + other as List<ElectronicAuctionsDetail>)

    override operator fun plus(others: List<ElectronicAuctionsDetail>) =
        ElectronicAuctionsDetails(this as List<ElectronicAuctionsDetail> + others)

    override fun updateBy(src: ElectronicAuctionsDetails) = ElectronicAuctionsDetails(update(dst = this, src = src))
}
