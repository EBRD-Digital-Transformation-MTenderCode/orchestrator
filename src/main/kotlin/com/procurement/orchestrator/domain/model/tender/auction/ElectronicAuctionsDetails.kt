package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.period.Period
import java.io.Serializable

data class ElectronicAuctionsDetails(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AuctionId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("auctionPeriod") @param:JsonProperty("auctionPeriod") val auctionPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("electronicAuctionModalities") @param:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: List<ElectronicAuctionModalities> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("electronicAuctionResult") @param:JsonProperty("electronicAuctionResult") val electronicAuctionResults: List<ElectronicAuctionResult> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("electronicAuctionProgress") @param:JsonProperty("electronicAuctionProgress") val electronicAuctionProgresses: List<ElectronicAuctionProgress> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ElectronicAuctionsDetails
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
