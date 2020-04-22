package com.procurement.orchestrator.domain.model.enquiry

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable
import java.time.LocalDateTime

data class Enquiry(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: EnquiryId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("author") @param:JsonProperty("author") val author: OrganizationReference? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("answer") @param:JsonProperty("answer") val answer: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateAnswered") @param:JsonProperty("dateAnswered") val dateAnswered: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId? = null
) : IdentifiableObject<Enquiry>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Enquiry
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Enquiry) = Enquiry(
        id = id,
        date = src.date or date,
        author = author updateBy src.author,
        title = src.title or title,
        description = src.description or description,
        answer = src.answer or answer,
        dateAnswered = src.dateAnswered or dateAnswered,
        relatedItem = src.relatedItem or relatedItem,
        relatedLot = src.relatedLot or relatedLot
    )
}
