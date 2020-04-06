package com.procurement.orchestrator.domain.model.organization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.party.PartyRole
import com.procurement.orchestrator.domain.model.person.Person
import java.io.Serializable

data class Organization(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<Identifier> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: Details? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("persones") @param:JsonProperty("persones") val persons: List<Person> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("buyerProfile") @param:JsonProperty("buyerProfile") val buyerProfile: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("roles") @param:JsonProperty("roles") val roles: List<PartyRole> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Organization
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
