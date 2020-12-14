package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.enquiry.Enquiries
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.conversion.Conversions
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import com.procurement.orchestrator.domain.model.tender.criteria.OtherCriteria
import com.procurement.orchestrator.domain.model.tender.target.Targets
import com.procurement.orchestrator.domain.model.updateBy
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable
import java.time.LocalDateTime

data class Tender(

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("id") @param:JsonProperty("id") val id: TenderId? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: TenderStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: TenderStatusDetails? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("otherCriteria") @param:JsonProperty("otherCriteria") val otherCriteria: OtherCriteria? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: Criteria = Criteria(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: Conversions = Conversions(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: Items = Items(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: Lots = Lots(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("lotGroups") @param:JsonProperty("lotGroups") val lotGroups: LotGroups = LotGroups(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("tenderPeriod") @param:JsonProperty("tenderPeriod") val tenderPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("enquiryPeriod") @param:JsonProperty("enquiryPeriod") val enquiryPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("standstillPeriod") @param:JsonProperty("standstillPeriod") val standstillPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardPeriod") @param:JsonProperty("awardPeriod") val awardPeriod: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("auctionPeriod") @param:JsonProperty("auctionPeriod") val auctionPeriod: Period? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("hasEnquiries") @param:JsonProperty("hasEnquiries") val hasEnquiries: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("enquiries") @param:JsonProperty("enquiries") val enquiries: Enquiries = Enquiries(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: Amendments = Amendments(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("submissionMethod") @param:JsonProperty("submissionMethod") val submissionMethod: SubmissionMethods = SubmissionMethods(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("submissionMethodDetails") @param:JsonProperty("submissionMethodDetails") val submissionMethodDetails: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("submissionMethodRationale") @param:JsonProperty("submissionMethodRationale") val submissionMethodRationales: SubmissionMethodRationales = SubmissionMethodRationales(),

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("requiresElectronicCatalogue") @param:JsonProperty("requiresElectronicCatalogue") val requiresElectronicCatalogue: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: ProcurementMethodModalities = ProcurementMethodModalities(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("electronicAuctions") @param:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("procedureOutsourcing") @param:JsonProperty("procedureOutsourcing") val procedureOutsourcing: ProcedureOutsourcing? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: Targets = Targets(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") val mainProcurementCategory: ProcurementCategory? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("additionalProcurementCategories") @param:JsonProperty("additionalProcurementCategories") val additionalProcurementCategories: AdditionalProcurementCategories = AdditionalProcurementCategories()

) : IdentifiableObject<Tender>, Serializable {

    override fun updateBy(src: Tender): Tender = Tender(
        id = src.id or id,
        title = src.title or title,
        description = src.description or description,
        status = src.status or status,
        statusDetails = src.statusDetails or statusDetails,
        date = src.date or date,
        criteria = criteria updateBy src.criteria,
        conversions = conversions updateBy src.conversions,
        items = items updateBy src.items,
        lots = lots updateBy src.lots,
        lotGroups = lotGroups updateBy src.lotGroups,
        tenderPeriod = tenderPeriod updateBy src.tenderPeriod,
        enquiryPeriod = enquiryPeriod updateBy src.enquiryPeriod,
        standstillPeriod = standstillPeriod updateBy src.standstillPeriod,
        awardPeriod = awardPeriod updateBy src.awardPeriod,
        auctionPeriod = auctionPeriod updateBy src.auctionPeriod,
        hasEnquiries = src.hasEnquiries or hasEnquiries,
        enquiries = enquiries updateBy src.enquiries,
        amendments = amendments updateBy src.amendments,
        documents = documents updateBy src.documents,
        awardCriteria = src.awardCriteria or awardCriteria,
        awardCriteriaDetails = src.awardCriteriaDetails or awardCriteriaDetails,
        submissionMethod = submissionMethod combineBy src.submissionMethod,
        submissionMethodDetails = src.submissionMethodDetails or submissionMethodDetails,
        submissionMethodRationales = submissionMethodRationales combineBy src.submissionMethodRationales,
        requiresElectronicCatalogue = src.requiresElectronicCatalogue or requiresElectronicCatalogue,
        procurementMethodModalities = procurementMethodModalities combineBy src.procurementMethodModalities,
        electronicAuctions = electronicAuctions updateBy src.electronicAuctions,
        procedureOutsourcing = procedureOutsourcing updateBy src.procedureOutsourcing,
        value = src.value or value,
        mainProcurementCategory = src.mainProcurementCategory or mainProcurementCategory
    )
}
