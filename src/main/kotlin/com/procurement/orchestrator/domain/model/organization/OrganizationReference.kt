package com.procurement.orchestrator.domain.model.organization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class OrganizationReference(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null
) : IdentifiableObject<OrganizationReference>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is OrganizationReference
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: OrganizationReference) = OrganizationReference(
        id = id,
        name = src.name or name
    )
}
