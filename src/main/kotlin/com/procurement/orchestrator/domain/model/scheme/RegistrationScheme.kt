package com.procurement.orchestrator.domain.model.scheme

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class RegistrationScheme(
    @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
    @field:JsonProperty("schemes") @param:JsonProperty("schemes") val schemes: Schemes
) : ComplexObject<RegistrationScheme>,
    Serializable {

    override fun updateBy(src: RegistrationScheme) = RegistrationScheme(
        country = src.country or country,
        schemes = src.schemes combineBy schemes
    )
}
