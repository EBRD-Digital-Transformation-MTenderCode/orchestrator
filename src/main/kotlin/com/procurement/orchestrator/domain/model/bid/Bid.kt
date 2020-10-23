package com.procurement.orchestrator.domain.model.bid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable
import java.time.LocalDateTime

data class Bid(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: BidId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: BidStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: BidStatusDetails? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("tenderers") @param:JsonProperty("tenderers") val tenderers: Organizations = Organizations(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: RelatedLots = RelatedLots(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses()
) : IdentifiableObject<Bid>, Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Bid
                && this.id == other.id
        else
            true
    }

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Bid) = Bid(
        id = id,
        date = src.date or date,
        status = src.status or status,
        statusDetails = src.statusDetails or statusDetails,
        tenderers = tenderers updateBy src.tenderers,
        value = src.value or value,
        documents = documents updateBy src.documents,
        relatedLots = relatedLots combineBy src.relatedLots,
        requirementResponses = requirementResponses updateBy src.requirementResponses
    )
}
