package com.procurement.orchestrator.domain.model.contract.milestone

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.item.ItemId
import java.io.Serializable
import java.time.LocalDateTime

data class Milestone(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: MilestoneId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    //TODO
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("type") @param:JsonProperty("type") val type: String? = null,

    //TODO
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedItems") @param:JsonProperty("relatedItems") val relatedItems: List<ItemId> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("additionalInformation") @param:JsonProperty("additionalInformation") val additionalInformation: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dueDate") @param:JsonProperty("dueDate") val dueDate: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedParties") @param:JsonProperty("relatedParties") val relatedParties: List<RelatedParty> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateModified") @param:JsonProperty("dateModified") val dateModified: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Milestone
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}


