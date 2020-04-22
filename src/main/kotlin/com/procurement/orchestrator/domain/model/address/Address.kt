package com.procurement.orchestrator.domain.model.address

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Address(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails? = null
) : ComplexObject<Address>, Serializable {

    override fun updateBy(src: Address): Address {
        return Address(
            streetAddress = src.streetAddress or streetAddress,
            postalCode = src.postalCode or postalCode,
            addressDetails = addressDetails updateBy src.addressDetails
        )
    }
}
