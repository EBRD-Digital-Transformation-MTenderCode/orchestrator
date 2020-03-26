package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.period.Period
import java.io.Serializable

data class ElectronicAuctionProgress(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ElectronicAuctionProgressId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("breakdown") @param:JsonProperty("breakdown") val breakdowns: List<ElectronicAuctionProgressBreakdown> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ElectronicAuctionProgress
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
