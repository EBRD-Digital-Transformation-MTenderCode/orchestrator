package com.procurement.orchestrator.domain.model.tender.auction

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ElectronicAuctions(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: List<ElectronicAuctionsDetails> = emptyList()
) : Serializable

