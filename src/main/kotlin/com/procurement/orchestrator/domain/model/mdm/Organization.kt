package com.procurement.orchestrator.domain.model.mdm

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Organization(
    @get:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @get:JsonProperty("name") @param:JsonProperty("name") val name: String
) : IdentifiableObject<Organization>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Organization
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Organization) = Organization(
        id = id,
        name = src.name or name
    )
}
