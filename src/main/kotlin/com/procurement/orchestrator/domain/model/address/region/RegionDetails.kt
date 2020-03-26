package com.procurement.orchestrator.domain.model.address.region

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class RegionDetails(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RegionScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RegionDetails
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
