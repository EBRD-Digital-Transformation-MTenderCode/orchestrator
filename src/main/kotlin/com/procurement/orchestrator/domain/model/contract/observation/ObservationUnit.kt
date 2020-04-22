package com.procurement.orchestrator.domain.model.contract.observation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class ObservationUnit(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: ObservationUnitScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ObservationUnitId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null
) : IdentifiableObject<ObservationUnit>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ObservationUnit
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun updateBy(src: ObservationUnit) = ObservationUnit(
        scheme = scheme,
        id = id,
        name = src.name or name
    )
}
