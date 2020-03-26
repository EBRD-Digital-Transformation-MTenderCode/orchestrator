package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.budget.BudgetSource
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequest
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponse
import com.procurement.orchestrator.domain.model.contract.milestone.Milestone
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import java.io.Serializable
import java.time.LocalDateTime

data class Contract(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("extendsContractId") @param:JsonProperty("extendsContractId") val extendsContractId: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("budgetSource") @param:JsonProperty("budgetSource") val budgetSource: List<BudgetSource> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: ValueTax? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateSigned") @param:JsonProperty("dateSigned") val dateSigned: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedProcesses") @param:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcess> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList(),

    @Deprecated("Need use 'amendments'")
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("countryOfOrigin") @param:JsonProperty("countryOfOrigin") val countryOfOrigin: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("lotVariant") @param:JsonProperty("lotVariant") val lotVariants: List<String> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("valueBreakdown") @param:JsonProperty("valueBreakdown") val valueBreakdowns: List<ValueBreakdown> = emptyList(),

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("agreedMetrics") @param:JsonProperty("agreedMetrics") val agreedMetrics: List<AgreedMetric> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("milestones") @param:JsonProperty("milestones") val milestones: List<Milestone> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("confirmationRequests") @param:JsonProperty("confirmationRequests") val confirmationRequests: List<ConfirmationRequest> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("confirmationResponses") @param:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Contract
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
