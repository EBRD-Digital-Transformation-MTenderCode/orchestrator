package com.procurement.orchestrator.domain.model.organization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
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
) : ComplexObject<ContactPoint>, Serializable {

    override fun updateBy(src: ContactPoint) = ContactPoint(
        name = src.name or name,
        email = src.email or email,
        telephone = src.telephone or telephone,
        faxNumber = src.faxNumber or faxNumber,
        url = src.url or url
    )
}
