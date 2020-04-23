package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class ElectronicAuctions(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: ElectronicAuctionsDetails = ElectronicAuctionsDetails()
) : ComplexObject<ElectronicAuctions>, Serializable {

    override fun updateBy(src: ElectronicAuctions) = ElectronicAuctions(
        details = details updateBy src.details
    )
}
