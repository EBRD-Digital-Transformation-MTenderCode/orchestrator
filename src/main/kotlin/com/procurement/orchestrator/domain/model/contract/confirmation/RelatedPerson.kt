package com.procurement.orchestrator.domain.model.contract.confirmation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class RelatedPerson(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RelatedPersonId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null
) : IdentifiableObject<RelatedPerson>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RelatedPerson
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: RelatedPerson) = RelatedPerson(
        id = id,
        name = src.name or name
    )
}
