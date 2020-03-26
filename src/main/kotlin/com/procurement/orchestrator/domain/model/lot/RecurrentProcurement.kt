package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.period.Period
import java.io.Serializable

data class RecurrentProcurement(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isRecurrent") @param:JsonProperty("isRecurrent") val isRecurrent: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("dates") @param:JsonProperty("dates") val dates: List<Period> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
) : Serializable
