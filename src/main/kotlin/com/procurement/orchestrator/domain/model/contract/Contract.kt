package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.budget.BudgetSources
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequests
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponses
import com.procurement.orchestrator.domain.model.contract.milestone.Milestones
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.OrganizationReferences
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable
import java.time.LocalDateTime

data class Contract(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: RelatedLots = RelatedLots(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: OrganizationReferences = OrganizationReferences(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("extendsContractId") @param:JsonProperty("extendsContractId") val extendsContractId: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("budgetSource") @param:JsonProperty("budgetSource") val budgetSource: BudgetSources = BudgetSources(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: ValueTax? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: Items = Items(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateSigned") @param:JsonProperty("dateSigned") val dateSigned: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedProcesses") @param:JsonProperty("relatedProcesses") val relatedProcesses: RelatedProcesses = RelatedProcesses(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: Amendments = Amendments(),

    @Deprecated("Need use 'amendments'")
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: RequirementResponses = RequirementResponses(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("countryOfOrigin") @param:JsonProperty("countryOfOrigin") val countryOfOrigin: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("lotVariant") @param:JsonProperty("lotVariant") val lotVariants: LotVariants = LotVariants(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("valueBreakdown") @param:JsonProperty("valueBreakdown") val valueBreakdowns: ValueBreakdowns = ValueBreakdowns(),

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("agreedMetrics") @param:JsonProperty("agreedMetrics") val agreedMetrics: AgreedMetrics = AgreedMetrics(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("milestones") @param:JsonProperty("milestones") val milestones: Milestones = Milestones(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("confirmationRequests") @param:JsonProperty("confirmationRequests") val confirmationRequests: ConfirmationRequests = ConfirmationRequests(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("confirmationResponses") @param:JsonProperty("confirmationResponses") val confirmationResponses: ConfirmationResponses = ConfirmationResponses()
) : IdentifiableObject<Contract>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Contract
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Contract) = Contract(
        id = id,
        date = src.date or date,
        awardId = src.awardId or awardId,
        relatedLots = relatedLots combineBy src.relatedLots,
        suppliers = suppliers updateBy src.suppliers,
        title = src.title or title,
        description = src.description or description,
        status = src.status or status,
        statusDetails = src.statusDetails or statusDetails,
        documents = documents updateBy src.documents,
        extendsContractId = src.extendsContractId or extendsContractId,
        budgetSource = budgetSource updateBy src.budgetSource,
        classification = classification updateBy src.classification,
        period = src.period or period,
        value = src.value or value,
        items = items updateBy src.items,
        dateSigned = src.dateSigned or dateSigned,
        relatedProcesses = relatedProcesses updateBy src.relatedProcesses,
        amendments = amendments updateBy src.amendments,
        amendment = amendment updateBy src.amendment,
        requirementResponses = requirementResponses updateBy src.requirementResponses,
        countryOfOrigin = src.countryOfOrigin or countryOfOrigin,
        lotVariants = lotVariants combineBy src.lotVariants,
        valueBreakdowns = valueBreakdowns updateBy src.valueBreakdowns,
        isFrameworkOrDynamic = src.isFrameworkOrDynamic or isFrameworkOrDynamic,
        agreedMetrics = agreedMetrics updateBy src.agreedMetrics,
        milestones = milestones updateBy src.milestones,
        confirmationRequests = confirmationRequests updateBy src.confirmationRequests,
        confirmationResponses = confirmationResponses updateBy src.confirmationResponses
    )
}
