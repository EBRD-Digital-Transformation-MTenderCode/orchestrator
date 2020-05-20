package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class ElectronicAuctionModality(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("url") @param:JsonProperty("url") val url: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("eligibleMinimumDifference") @param:JsonProperty("eligibleMinimumDifference") val eligibleMinimumDifference: Value? = null
) : ComplexObject<ElectronicAuctionModality>, Serializable {

    override fun updateBy(src: ElectronicAuctionModality) = ElectronicAuctionModality(
        url = src.url or url,
        eligibleMinimumDifference = src.eligibleMinimumDifference or eligibleMinimumDifference
    )
}
