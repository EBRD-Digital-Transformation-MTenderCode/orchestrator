package com.procurement.orchestrator.domain.model.organization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ContactPoint(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("email") @param:JsonProperty("email") val email: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("url") @param:JsonProperty("url") val url: String? = null
) : Serializable
