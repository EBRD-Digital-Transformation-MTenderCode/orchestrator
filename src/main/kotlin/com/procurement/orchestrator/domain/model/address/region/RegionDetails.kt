package com.procurement.orchestrator.domain.model.address.region

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class RegionDetails(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RegionScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : IdentifiableObject<RegionDetails>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RegionDetails
            && this.scheme.equals(other.scheme, true)
            && this.id.equals(other.id, true)

    override fun hashCode(): Int {
        var result = scheme.toUpperCase().hashCode()
        result = 31 * result + id.toUpperCase().hashCode()
        return result
    }

    override fun updateBy(src: RegionDetails) = RegionDetails(
        scheme = src.scheme,
        id = src.id,
        description = src.description or description,
        uri = src.uri or uri
    )
}
