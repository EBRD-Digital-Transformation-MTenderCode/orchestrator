package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Change(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("property") @param:JsonProperty("property") val property: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("formerValue") @param:JsonProperty("formerValue") val formerValue: Any? = null
) : Serializable
