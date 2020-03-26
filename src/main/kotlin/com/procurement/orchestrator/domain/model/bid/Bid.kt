package com.procurement.orchestrator.domain.model.bid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
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
    @field:JsonProperty("tenderers") @param:JsonProperty("tenderers") val tenderers: List<OrganizationReference> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Bid
                && this.id == other.id
        else
            true
    }

    override fun hashCode(): Int = id.hashCode()
}
