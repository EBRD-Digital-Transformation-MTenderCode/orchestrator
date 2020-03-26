package com.procurement.orchestrator.domain.model.organization.datail.legalform

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class LegalForm(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: LegalFormScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: LegalFormId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is LegalForm
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
