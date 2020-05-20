package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.updateBy
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
    @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: Organizations = Organizations(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: Items = Items(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("contractPeriod") @param:JsonProperty("contractPeriod") val contractPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: Amendments = Amendments(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("reviewProceedings") @param:JsonProperty("reviewProceedings") val reviewProceedings: ReviewProceedings? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: RelatedLots = RelatedLots(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("weightedValue") @param:JsonProperty("weightedValue") val weightedValue: Value? = null
) : IdentifiableObject<Award>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Award
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Award) = Award(
        id = id,
        title = src.title or title,
        description = src.description or description,
        status = src.status or status,
        statusDetails = src.statusDetails or statusDetails,
        date = src.date or date,
        value = src.value or value,
        suppliers = suppliers updateBy src.suppliers,
        items = items updateBy src.items,
        contractPeriod = contractPeriod updateBy src.contractPeriod,
        documents = documents updateBy src.documents,
        amendments = amendments updateBy src.amendments,
        amendment = amendment updateBy src.amendment,
        requirementResponses = requirementResponses updateBy src.requirementResponses,
        reviewProceedings = reviewProceedings updateBy src.reviewProceedings,
        relatedLots = relatedLots combineBy src.relatedLots,
        relatedBid = src.relatedBid or relatedBid,
        weightedValue = src.weightedValue or weightedValue
    )
}
