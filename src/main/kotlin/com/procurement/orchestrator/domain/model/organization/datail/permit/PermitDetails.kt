package com.procurement.orchestrator.domain.model.organization.datail.permit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.period.Period
import java.io.Serializable

data class PermitDetails(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("issuedBy") @param:JsonProperty("issuedBy") val issuedBy: Issue? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("issuedThought") @param:JsonProperty("issuedThought") val issuedThought: Issue? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("validityPeriod") @param:JsonProperty("validityPeriod") val validityPeriod: Period? = null
) : Serializable
