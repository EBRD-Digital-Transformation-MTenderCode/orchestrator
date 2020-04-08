package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.enquiry.Enquiry
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.auction.ElectronicAuctions
import com.procurement.orchestrator.domain.model.tender.conversion.Conversion
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import java.io.Serializable

data class Tender(

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner?,

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

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criteria> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("lotGroups") @param:JsonProperty("lotGroups") val lotGroups: List<LotGroup> = emptyList(),

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
    @get:JsonProperty("hasEnquiries") @param:JsonProperty("hasEnquiries") val hasEnquiries: Boolean? = false,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("enquiries") @param:JsonProperty("enquiries") val enquiries: List<Enquiry> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("submissionMethod") @param:JsonProperty("submissionMethod") val submissionMethod: List<String> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("submissionMethodDetails") @param:JsonProperty("submissionMethodDetails") val submissionMethodDetails: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("submissionMethodRationale") @param:JsonProperty("submissionMethodRationale") val submissionMethodRationale: List<String> = emptyList(),

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("requiresElectronicCatalogue") @param:JsonProperty("requiresElectronicCatalogue") val requiresElectronicCatalogue: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("electronicAuctions") @param:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions? = null
) : Serializable
