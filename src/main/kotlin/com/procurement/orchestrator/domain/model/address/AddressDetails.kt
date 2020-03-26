package com.procurement.orchestrator.domain.model.address

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import java.io.Serializable

data class AddressDetails(
    @field:JsonProperty("country") @param:JsonProperty("country") val country: CountryDetails,
    @field:JsonProperty("region") @param:JsonProperty("region") val region: RegionDetails,
    @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: LocalityDetails
) : Serializable
