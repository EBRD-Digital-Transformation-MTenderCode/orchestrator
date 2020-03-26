package com.procurement.orchestrator.domain.model.identifier

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Identifier(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: IdentifierScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: IdentifierId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Identifier
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
