package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Option(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("hasOptions") @param:JsonProperty("hasOptions") val hasOptions: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("optionDetails") @param:JsonProperty("optionDetails") val optionDetails: String? = null
) : Serializable
