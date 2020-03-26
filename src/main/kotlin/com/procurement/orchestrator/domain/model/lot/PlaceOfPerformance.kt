package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.address.Address
import java.io.Serializable

data class PlaceOfPerformance(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("nutScode") @param:JsonProperty("nutScode") val nutScode: String? = null
) : Serializable
