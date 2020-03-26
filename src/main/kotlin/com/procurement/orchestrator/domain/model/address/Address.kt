package com.procurement.orchestrator.domain.model.address

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Address(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails? = null
) : Serializable
