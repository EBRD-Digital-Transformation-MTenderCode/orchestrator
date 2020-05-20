package com.procurement.orchestrator.domain.model.bid

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BidsStatistics(values: List<BidsStatistic> = emptyList()) : List<BidsStatistic> by values,
                                                                  IdentifiableObjects<BidsStatistic, BidsStatistics>,
                                                                  Serializable {

    constructor(value: BidsStatistic) : this(listOf(value))

    override operator fun plus(other: BidsStatistics) =
        BidsStatistics(this as List<BidsStatistic> + other as List<BidsStatistic>)

    override operator fun plus(others: List<BidsStatistic>) = BidsStatistics(this as List<BidsStatistic> + others)

    override fun updateBy(src: BidsStatistics) = BidsStatistics(update(dst = this, src = src))
}
