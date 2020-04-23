package com.procurement.orchestrator.domain.model.bid

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class BidsDetails(values: List<Bid> = emptyList()) : List<Bid> by values,
                                                     IdentifiableObjects<Bid, BidsDetails>,
                                                     Serializable {

    override fun updateBy(src: BidsDetails) = BidsDetails(update(dst = this, src = src))
}
