package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.classification.Classifications
import com.procurement.orchestrator.domain.model.contract.RelatedProcess
import com.procurement.orchestrator.domain.model.contract.RelatedProcessId
import com.procurement.orchestrator.domain.model.contract.RelatedProcessIdentifier
import com.procurement.orchestrator.domain.model.contract.RelatedProcessScheme
import com.procurement.orchestrator.domain.model.contract.RelatedProcessType
import com.procurement.orchestrator.domain.model.contract.RelatedProcessTypes
import com.procurement.orchestrator.domain.model.contract.observation.Dimensions
import com.procurement.orchestrator.domain.model.contract.observation.Observation
import com.procurement.orchestrator.domain.model.contract.observation.ObservationId
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Options
import com.procurement.orchestrator.domain.model.lot.RecurrentProcurements
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.lot.Renewals
import com.procurement.orchestrator.domain.model.lot.Variant
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.AwardCriteria
import com.procurement.orchestrator.domain.model.tender.AwardCriteriaDetails
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModality
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.TenderStatus
import com.procurement.orchestrator.domain.model.tender.TenderStatusDetails
import com.procurement.orchestrator.domain.model.tender.conversion.Conversion
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionId
import com.procurement.orchestrator.domain.model.tender.conversion.ConversionsRelatesTo
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficient
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficients
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.model.tender.criteria.Criterion
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirement
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.Requirements
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.rate.CoefficientRateSerializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.conversion.coefficient.value.CoefficientValueSerializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.RequirementsDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.RequirementsSerializer
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantityDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantitySerializer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime
import com.procurement.orchestrator.domain.model.tender.target.Target as DomainTarget

abstract class CreatePcrAction : FunctionalAction<CreatePcrAction.Params, CreatePcrAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createPcr"
    override val target: Target<Result> = Target.single()

    class Params(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid?,

        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("stateFE") @param:JsonProperty("stateFE") val stateFE: String,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {

        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Value?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails?,

            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<Target>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>?,

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>?

        ) {

            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
            )

            data class Value(
                @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
            )

            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: Variant?
            ) {

                data class Variant(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("variantsDetails") @param:JsonProperty("variantsDetails") val variantsDetails: String?
                )
            }

            data class Item(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @JsonDeserialize(using = QuantityDeserializer::class)
                @JsonSerialize(using = QuantitySerializer::class)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Quantity?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId?

            ) {
                data class Unit(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
                )
            }

            data class Target(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?,

                @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<Observation>
            ) {
                data class Observation(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: ObservationId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("measure") @param:JsonProperty("measure") val measure: Any?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: ObservationUnit?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("dimensions") @param:JsonProperty("dimensions") val dimensions: Dimensions?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("notes") @param:JsonProperty("notes") val notes: String?,

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
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
                    )
                }
            }

            data class Criterion(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo?,

                @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>

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

            data class Conversion(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: ConversionId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String?,

                @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<Coefficient>,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?

            ) {
                data class Coefficient(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonDeserialize(using = CoefficientValueDeserializer::class)
                    @JsonSerialize(using = CoefficientValueSerializer::class)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue?,

                    @JsonDeserialize(using = CoefficientRateDeserializer::class)
                    @JsonSerialize(using = CoefficientRateSerializer::class)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate?
                )
            }

            data class Document(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>?
            )
        }
    }

    class Result(
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender,
        @field:JsonProperty("relatedProcesses") @param:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcess>
    ) : Serializable {

        data class Tender(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: TenderStatus,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: TenderStatusDetails,
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
            @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria,
            @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails,

            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<Target>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>?,

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>?

        ) : Serializable {

            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String
            ) : Serializable

            data class Value(
                @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
            ) : Serializable

            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

                @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: Variant
            ) : Serializable {

                data class Variant(
                    @field:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("variantsDetails") @param:JsonProperty("variantsDetails") val variantsDetails: String?
                ) : Serializable
            }

            data class Item(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                @JsonDeserialize(using = QuantityDeserializer::class)
                @JsonSerialize(using = QuantitySerializer::class)
                @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Quantity,

                @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,
                @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,
                @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId

            ) : Serializable {
                data class Unit(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                ) : Serializable
            }

            data class Target(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
                @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<Observation>
            ) : Serializable {
                data class Observation(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: ObservationId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period?,

                    @field:JsonProperty("measure") @param:JsonProperty("measure") val measure: Any,
                    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: ObservationUnit,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("dimensions") @param:JsonProperty("dimensions") val dimensions: Dimensions?,

                    @field:JsonProperty("notes") @param:JsonProperty("notes") val notes: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("relatedRequirementId") @param:JsonProperty("relatedRequirementId") val relatedRequirementId: String?

                ) : Serializable {

                    data class Dimensions(
                        @field:JsonProperty("requirementClassIdPR") @param:JsonProperty("requirementClassIdPR") val requirementClassIdPR: String
                    ) : Serializable

                    data class Period(
                        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                        @JsonSerialize(using = JsonDateTimeSerializer::class)
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime?,

                        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                        @JsonSerialize(using = JsonDateTimeSerializer::class)
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime?
                    ) : Serializable

                    data class ObservationUnit(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    ) : Serializable
                }
            }

            data class Criterion(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo?,

                @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource,

                @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>

            ) : Serializable {
                data class RequirementGroup(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                    @JsonDeserialize(using = RequirementsDeserializer::class)
                    @JsonSerialize(using = RequirementsSerializer::class)
                    @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
                ) : Serializable
            }

            data class Conversion(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: ConversionId,

                @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo,

                @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,

                @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

                @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<Coefficient>,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?

            ) : Serializable {
                data class Coefficient(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonDeserialize(using = CoefficientValueDeserializer::class)
                    @JsonSerialize(using = CoefficientValueSerializer::class)
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue,

                    @JsonDeserialize(using = CoefficientRateDeserializer::class)
                    @JsonSerialize(using = CoefficientRateSerializer::class)
                    @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
                ) : Serializable
            }

            data class Document(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>?
            ) : Serializable
        }

        data class RelatedProcess(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: RelatedProcessId,
            @field:JsonProperty("relationship") @param:JsonProperty("relationship") val relationship: List<RelatedProcessType>,
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RelatedProcessScheme,
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: RelatedProcessIdentifier,
            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String

        ) : Serializable
    }

    object ResponseConverter {

        object RelatedProcess {

            fun toDomain(relatedProcess: Result.RelatedProcess): com.procurement.orchestrator.domain.model.contract.RelatedProcess =
                RelatedProcess(
                    id = relatedProcess.id,
                    relationship = RelatedProcessTypes(relatedProcess.relationship),
                    scheme = relatedProcess.scheme,
                    identifier = relatedProcess.identifier,
                    uri = relatedProcess.uri
                )
        }

        object Classifications {
            fun toDomain(classification: Result.Tender.Classification): Classification =
                Classification(
                    id = classification.id,
                    scheme = classification.scheme,
                    description = classification.description,
                    uri = null
                )
        }

        object Items {
            fun toDomain(item: Result.Tender.Item): Item =
                Item(
                    id = item.id,
                    description = item.description,
                    unit = item.unit.convert(),
                    internalId = item.internalId,
                    quantity = item.quantity,
                    classification = Classifications.toDomain(item.classification),
                    relatedLot = item.relatedLot,
                    deliveryAddress = null,
                    additionalClassifications = Classifications()
                )

            private fun Result.Tender.Item.Unit.convert(): Unit =
                Unit(
                    id = this.id,
                    name = this.name
                )
        }

        object Lots {
            fun toDomain(lot: Result.Tender.Lot): Lot =
                Lot(
                    id = lot.id,
                    description = lot.description,
                    internalId = lot.internalId,
                    classification = Classifications.toDomain(lot.classification),
                    title = lot.title,
                    variants = lot.variants.convert(),
                    status = null,
                    statusDetails = null,
                    value = null,
                    contractPeriod = null,
                    placeOfPerformance = null,
                    options = Options(),
                    recurrentProcurement = RecurrentProcurements(),
                    renewals = Renewals()
                )

            private fun Result.Tender.Lot.Variant.convert(): Variant =
                Variant(
                    hasVariants = this.hasVariants,
                    variantDetails = this.variantsDetails
                )
        }

        object Targets {
            fun toDomain(target: Result.Tender.Target): DomainTarget =
                DomainTarget(
                    id = target.id,
                    title = target.title,
                    relatesTo = target.relatesTo,
                    relatedItem = target.relatedItem,
                    observations = target.observations
                        .map { it.convert() }
                        .let { Observations(it) }
                )

            private fun Result.Tender.Target.Observation.convert(): Observation =
                Observation(
                    id = this.id,
                    unit = this.unit.convert(),
                    period = this.period?.convert(),
                    dimensions = this.dimensions?.convert(),
                    measure = this.measure,
                    notes = this.notes,
                    relatedRequirementId = this.relatedRequirementId
                )

            private fun Result.Tender.Target.Observation.ObservationUnit.convert(): Unit =
                Unit(
                    id = this.id,
                    name = this.name
                )

            private fun Result.Tender.Target.Observation.Period.convert(): Period =
                Period(
                    startDate = this.startDate,
                    endDate = this.endDate
                )

            private fun Result.Tender.Target.Observation.Dimensions.convert(): Dimensions =
                Dimensions(
                    requirementClassIdPR = this.requirementClassIdPR
                )
        }

        object Criteria {
            fun toDomain(criterion: Result.Tender.Criterion): Criterion =
                Criterion(
                    id = criterion.id,
                    description = criterion.description,
                    title = criterion.title,
                    relatedItem = criterion.relatedItem,
                    relatesTo = criterion.relatesTo,
                    requirementGroups = criterion.requirementGroups
                        .map { it.convert() }
                        .let { RequirementGroups(it) },
                    source = criterion.source
                )

            private fun Result.Tender.Criterion.RequirementGroup.convert(): RequirementGroup =
                RequirementGroup(
                    id = this.id,
                    description = this.description,
                    requirements = Requirements(this.requirements)
                )
        }

        object Conversions {
            fun toDomain(conversion: Result.Tender.Conversion): Conversion =
                Conversion(
                    id = conversion.id,
                    description = conversion.description,
                    relatedItem = conversion.relatedItem,
                    relatesTo = conversion.relatesTo,
                    rationale = conversion.rationale,
                    coefficients = conversion.coefficients
                        .map { it.convert() }
                        .let { Coefficients(it) }
                )

            private fun Result.Tender.Conversion.Coefficient.convert(): Coefficient =
                Coefficient(
                    id = this.id,
                    value = this.value,
                    coefficient = this.coefficient
                )
        }

        object Documents {
            fun toDomain(document: Result.Tender.Document): Document =
                Document(
                    id = document.id,
                    description = document.description,
                    title = document.title,
                    documentType = document.documentType,
                    relatedLots = RelatedLots(document.relatedLots.orEmpty())
                )
        }
    }

    object RequestConverter {
        fun fromDomain(tender: Tender): Params.Tender =
            Params.Tender(
                title = tender.title,
                description = tender.description,
                classification = tender.classification
                    ?.let { classification ->
                        Params.Tender.Classification(
                            id = classification.id,
                            scheme = classification.scheme,
                            description = classification.description
                        )
                    },
                value = tender.value
                    ?.let { value ->
                        Params.Tender.Value(
                            currency = value.currency
                        )
                    },
                awardCriteria = tender.awardCriteria,
                awardCriteriaDetails = tender.awardCriteriaDetails,
                lots = tender.lots
                    .map { lot ->
                        Params.Tender.Lot(
                            id = lot.id,
                            internalId = lot.internalId,
                            title = lot.title,
                            description = lot.description,
                            classification = lot.classification
                                ?.let { classification ->
                                    Params.Tender.Classification(
                                        id = classification.id,
                                        scheme = classification.scheme,
                                        description = classification.description
                                    )
                                },
                            variants = lot.variants
                                ?.let { variants ->
                                    Params.Tender.Lot.Variant(
                                        hasVariants = variants.hasVariants,
                                        variantsDetails = variants.variantDetails
                                    )
                                }
                        )
                    },
                items = tender.items
                    .map { item ->
                        Params.Tender.Item(
                            id = item.id,
                            internalId = item.internalId,
                            description = item.description,
                            quantity = item.quantity,
                            classification = item.classification
                                ?.let { classification ->
                                    Params.Tender.Classification(
                                        id = classification.id,
                                        scheme = classification.scheme,
                                        description = classification.description
                                    )
                                },
                            unit = item.unit
                                ?.let { unit ->
                                    Params.Tender.Item.Unit(
                                        id = unit.id,
                                        name = unit.name
                                    )
                                },
                            relatedLot = item.relatedLot
                        )
                    },
                targets = tender.targets
                    .map { target ->
                        Params.Tender.Target(
                            id = target.id,
                            title = target.title,
                            relatesTo = target.relatesTo,
                            relatedItem = target.relatedItem,
                            observations = target.observations
                                .map { observation ->
                                    Params.Tender.Target.Observation(
                                        id = observation.id,
                                        period = observation.period
                                            ?.let { period ->
                                                Params.Tender.Target.Observation.Period(
                                                    startDate = period.startDate,
                                                    endDate = period.endDate
                                                )
                                            },
                                        measure = observation.measure,
                                        notes = observation.notes,
                                        unit = observation.unit
                                            ?.let { unit ->
                                                Params.Tender.Target.Observation.ObservationUnit(
                                                    id = unit.id,
                                                    name = unit.name
                                                )
                                            },
                                        dimensions = observation.dimensions
                                            ?.let { dimensions ->
                                                Params.Tender.Target.Observation.Dimensions(
                                                    requirementClassIdPR = dimensions.requirementClassIdPR
                                                )
                                            },
                                        relatedRequirementId = observation.relatedRequirementId
                                    )
                                }
                        )
                    },
                criteria = tender.criteria
                    .map { criterion ->
                        Params.Tender.Criterion(
                            id = criterion.id,
                            title = criterion.title,
                            description = criterion.description,
                            relatedItem = criterion.relatedItem,
                            relatesTo = criterion.relatesTo,
                            requirementGroups = criterion.requirementGroups
                                .map { requirementGroup ->
                                    Params.Tender.Criterion.RequirementGroup(
                                        id = requirementGroup.id,
                                        description = requirementGroup.description,
                                        requirements = requirementGroup.requirements
                                    )
                                }

                        )
                    },
                conversions = tender.conversions
                    .map { conversion ->
                        Params.Tender.Conversion(
                            id = conversion.id,
                            description = conversion.description,
                            relatedItem = conversion.relatedItem,
                            relatesTo = conversion.relatesTo,
                            rationale = conversion.rationale,
                            coefficients = conversion.coefficients
                                .map { coefficient ->
                                    Params.Tender.Conversion.Coefficient(
                                        id = coefficient.id,
                                        value = coefficient.value,
                                        coefficient = coefficient.coefficient
                                    )
                                }
                        )
                    },
                procurementMethodModalities = tender.procurementMethodModalities,
                documents = tender.documents
                    .map { document ->
                        Params.Tender.Document(
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            documentType = document.documentType,
                            relatedLots = document.relatedLots.toList()
                        )
                    }
            )
    }
}
