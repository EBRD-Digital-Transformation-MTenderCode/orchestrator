package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Renewal(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("hasRenewals") @param:JsonProperty("hasRenewals") val hasRenewals: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maxNumber") @param:JsonProperty("maxNumber") val maxNumber: Int? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("renewalConditions") @param:JsonProperty("renewalConditions") val renewalConditions: String? = null
) : Serializable
