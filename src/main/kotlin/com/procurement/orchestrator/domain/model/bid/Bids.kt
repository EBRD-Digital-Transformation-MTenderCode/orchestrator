package com.procurement.orchestrator.domain.model.bid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class Bids(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("statistics") @param:JsonProperty("statistics") val statistics: BidsStatistics = BidsStatistics(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: BidsDetails = BidsDetails()
) : ComplexObject<Bids>, Serializable {

    override fun updateBy(src: Bids) = Bids(
        statistics = statistics updateBy src.statistics,
        details = details updateBy src.details
    )
}
