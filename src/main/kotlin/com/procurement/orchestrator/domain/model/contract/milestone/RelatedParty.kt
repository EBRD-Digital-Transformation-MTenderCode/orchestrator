package com.procurement.orchestrator.domain.model.contract.milestone

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class RelatedParty(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RelatedPartyId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RelatedParty
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
