package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Verification(
    //TODO null
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("type") @param:JsonProperty("type") val type: String? = null,

    //TODO null
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: String? = null,

    //TODO null
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null
) : Serializable
