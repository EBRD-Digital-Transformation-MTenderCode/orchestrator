package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable
import java.time.LocalDateTime

data class Award(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<OrganizationReference> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("contractPeriod") @param:JsonProperty("contractPeriod") val contractPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("reviewProceedings") @param:JsonProperty("reviewProceedings") val reviewProceedings: ReviewProceedings? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("weightedValue") @param:JsonProperty("weightedValue") val weightedValue: Value? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Award
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
