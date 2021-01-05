package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.contract.observation.ObservationId
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.tender.AwardCriteria
import com.procurement.orchestrator.domain.model.tender.AwardCriteriaDetails
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModality
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionId
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionsRelatesTo
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateSerializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueSerializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.RequirementsDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.RequirementsSerializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantityDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantitySerializer
import java.time.LocalDateTime

object CreatePreAwardCatalogRequest {

    class Request {

        class Payload(
            @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
        ) {

            data class Tender(
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,
                @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria,
                @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails,
                @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<Target>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criteria>?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("tenderPeriod") @param:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod?,

                @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("electronicAuctions") @param:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>?

            ) {

                data class Classification(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                )

                data class Lot(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                    @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: List<Variant>,
                    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?
                ) {

                    data class Variant(
                        @field:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("variantsDetails") @param:JsonProperty("variantsDetails") val variantsDetails: String?
                    )
                }

                data class Item(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                    @JsonDeserialize(using = QuantityDeserializer::class)
                    @JsonSerialize(using = QuantitySerializer::class)
                    @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Quantity,

                    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,
                    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?
                ) {
                    data class Unit(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                    )
                }

                data class Target(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
                    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
                    @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<Observation>
                ) {
                    data class Observation(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: ObservationId,
                        @field:JsonProperty("measure") @param:JsonProperty("measure") val measure: Any,
                        @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: ObservationUnit,
                        @field:JsonProperty("notes") @param:JsonProperty("notes") val notes: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("period") @param:JsonProperty("period") val period: Period?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("dimensions") @param:JsonProperty("dimensions") val dimensions: Dimensions?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("relatedRequirementId") @param:JsonProperty("relatedRequirementId") val relatedRequirementId: String?

                    ) {

                        data class Dimensions(
                            @field:JsonProperty("requirementClassIdPR") @param:JsonProperty("requirementClassIdPR") val requirementClassIdPR: String
                        )

                        data class Period(
                            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                            @JsonSerialize(using = JsonDateTimeSerializer::class)
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime?,

                            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                            @JsonSerialize(using = JsonDateTimeSerializer::class)
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime?
                        )

                        data class ObservationUnit(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                        )
                    }
                }

                data class Criteria(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo,

                    @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
                ) {
                    data class RequirementGroup(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                        @JsonDeserialize(using = RequirementsDeserializer::class)
                        @JsonSerialize(using = RequirementsSerializer::class)
                        @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
                    )
                }

                data class TenderPeriod(
                    @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                    @JsonSerialize(using = JsonDateTimeSerializer::class)
                    @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
                )

                data class ElectronicAuctions(
                    @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
                ) {

                    data class Detail(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String,
                        @field:JsonProperty("electronicAuctionModalities") @param:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: List<Modalities>
                    ) {

                        data class Modalities(
                            @field:JsonProperty("eligibleMinimumDifference") @param:JsonProperty("eligibleMinimumDifference") val eligibleMinimumDifference: EligibleMinimumDifference
                        ) {

                            data class EligibleMinimumDifference(
                                @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                            )
                        }
                    }
                }

                data class Conversion(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: ConversionId,
                    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo,
                    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
                    @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,
                    @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<Coefficient>,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?

                ) {
                    data class Coefficient(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                        @JsonDeserialize(using = CoefficientValueDeserializer::class)
                        @JsonSerialize(using = CoefficientValueSerializer::class)
                        @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue,

                        @JsonDeserialize(using = CoefficientRateDeserializer::class)
                        @JsonSerialize(using = CoefficientRateSerializer::class)
                        @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
                    )
                }

                data class RequirementResponse(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonDeserialize(using = RequirementValueDeserializer::class)
                    @JsonSerialize(using = RequirementValueSerializer::class)
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,

                    @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
                    @field:JsonProperty("relatedCandidate") @param:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate
                ) {

                    data class Requirement(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
                    )

                    data class RelatedCandidate(
                        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    ) {

                        data class Identifier(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                        )
                    }
                }

                data class Document(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @field:JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>?
                )
            }
        }
    }
}
