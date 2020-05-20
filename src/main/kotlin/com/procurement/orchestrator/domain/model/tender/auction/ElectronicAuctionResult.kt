package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class ElectronicAuctionResult(
    @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null
) : IdentifiableObject<ElectronicAuctionResult>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ElectronicAuctionResult
            && this.relatedBid == other.relatedBid

    override fun hashCode(): Int = relatedBid.hashCode()

    override fun updateBy(src: ElectronicAuctionResult) = ElectronicAuctionResult(
        relatedBid = relatedBid,
        value = src.value or value
    )
}
