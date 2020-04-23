package com.procurement.orchestrator.domain.model.bid

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BidsStatistics(values: List<BidsStatistic> = emptyList()) : List<BidsStatistic> by values,
                                                                  IdentifiableObjects<BidsStatistic, BidsStatistics>,
                                                                  Serializable {

    override fun updateBy(src: BidsStatistics) = BidsStatistics(update(dst = this, src = src))
}
