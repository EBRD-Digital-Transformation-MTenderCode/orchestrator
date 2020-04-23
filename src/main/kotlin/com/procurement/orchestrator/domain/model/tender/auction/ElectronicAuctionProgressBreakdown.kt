package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable
import java.time.LocalDateTime

data class ElectronicAuctionProgressBreakdown(
    @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId,

    //TODO type
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null
) : IdentifiableObject<ElectronicAuctionProgressBreakdown>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ElectronicAuctionProgressBreakdown
            && this.relatedBid == other.relatedBid

    override fun hashCode(): Int = relatedBid.hashCode()

    override fun updateBy(src: ElectronicAuctionProgressBreakdown) = ElectronicAuctionProgressBreakdown(
        relatedBid = relatedBid,
        status = src.status or status,
        dateMet = src.dateMet or dateMet,
        value = src.value or value
    )
}
