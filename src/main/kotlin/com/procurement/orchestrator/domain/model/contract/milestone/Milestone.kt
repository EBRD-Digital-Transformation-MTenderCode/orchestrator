package com.procurement.orchestrator.domain.model.contract.milestone

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import com.procurement.orchestrator.domain.model.item.RelatedItems
import com.procurement.orchestrator.domain.model.or
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
    @field:JsonProperty("relatedItems") @param:JsonProperty("relatedItems") val relatedItems: RelatedItems = RelatedItems(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("additionalInformation") @param:JsonProperty("additionalInformation") val additionalInformation: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dueDate") @param:JsonProperty("dueDate") val dueDate: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedParties") @param:JsonProperty("relatedParties") val relatedParties: RelatedParties = RelatedParties(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateModified") @param:JsonProperty("dateModified") val dateModified: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime? = null
) : IdentifiableObject<Milestone>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Milestone
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Milestone) = Milestone(
        id = id,
        title = src.title or title,
        description = src.description or description,
        type = src.type or type,
        status = src.status or status,
        relatedItems = relatedItems combineBy src.relatedItems,
        additionalInformation = src.additionalInformation or additionalInformation,
        dueDate = src.dueDate or dueDate,
        relatedParties = relatedParties updateBy src.relatedParties,
        dateModified = src.dateModified or dateModified,
        dateMet = src.dateMet or dateMet
    )
}


