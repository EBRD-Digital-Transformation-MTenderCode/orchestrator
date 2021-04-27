package com.procurement.orchestrator.domain.model.masterdata.access

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import java.io.Serializable

data class Buyer(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
) : IdentifiableObject<Buyer>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Buyer
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Buyer) = Buyer(
        id = id,
        name = src.name or name,
        owner = src.owner or owner
    )
}
