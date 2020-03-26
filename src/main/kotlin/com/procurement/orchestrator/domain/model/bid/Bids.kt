package com.procurement.orchestrator.domain.model.bid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Bids(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("statistics") @param:JsonProperty("statistics") val statistics: List<BidsStatistic> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Bid> = emptyList()
) : Serializable
