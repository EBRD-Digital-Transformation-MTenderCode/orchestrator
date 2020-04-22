package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class ElectronicAuctionsDetail(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AuctionId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("auctionPeriod") @param:JsonProperty("auctionPeriod") val auctionPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("electronicAuctionModalities") @param:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: ElectronicAuctionModalities = ElectronicAuctionModalities(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("electronicAuctionResult") @param:JsonProperty("electronicAuctionResult") val electronicAuctionResults: ElectronicAuctionResults = ElectronicAuctionResults(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("electronicAuctionProgress") @param:JsonProperty("electronicAuctionProgress") val electronicAuctionProgresses: ElectronicAuctionProgresses = ElectronicAuctionProgresses()
) : IdentifiableObject<ElectronicAuctionsDetail>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ElectronicAuctionsDetail
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: ElectronicAuctionsDetail) = ElectronicAuctionsDetail(
        id = id,
        relatedLot = src.relatedLot or relatedLot,
        auctionPeriod = auctionPeriod updateBy src.auctionPeriod,
        electronicAuctionModalities = electronicAuctionModalities combineBy src.electronicAuctionModalities,
        electronicAuctionResults = electronicAuctionResults updateBy src.electronicAuctionResults,
        electronicAuctionProgresses = electronicAuctionProgresses updateBy src.electronicAuctionProgresses
    )
}
