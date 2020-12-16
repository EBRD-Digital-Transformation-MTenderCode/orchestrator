package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.contract.AgreedMetric
import com.procurement.orchestrator.domain.model.contract.AgreedMetrics
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.observation.Observation
import com.procurement.orchestrator.domain.model.contract.observation.Observations
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaRelatesTo
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class CreatePacsAction : FunctionalAction<CreatePacsAction.Params, CreatePacsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createPacs"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids?,

        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner
    ) {
        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("targets") @field:JsonProperty("targets") val targets: List<Target>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("criteria") @field:JsonProperty("criteria") val criteria: List<Criteria>
        ) {
            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            )

            data class Target(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
            ) {
                data class Observation(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String?
                ) {
                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                    )
                }
            }

            data class Criteria(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementGroups") @field:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
            ) {
                data class RequirementGroup(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("requirements") @field:JsonProperty("requirements") val requirements: List<Requirement>
                ) {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String
                    )
                }
            }
        }

        data class Award(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
        ) {
            data class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
            )
        }

        data class Bids(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
            ) {
                data class Tenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                )

                data class RequirementResponse(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                ) {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                    )

                    data class Period(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                    )
                }
            }
        }
    }

    data class Result(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("token") @field:JsonProperty("token") val token: Token?
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: AwardId?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("agreedMetrics ") @field:JsonProperty("agreedMetrics ") val agreedMetrics: List<AgreedMetric>?
        ) {
            data class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )

            data class AgreedMetric(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
            ) {
                data class Observation(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,
                    @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: String,
                    @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?
                ) {
                    data class Period(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                    )

                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    )
                }
            }
        }
    }
}

fun CreatePacsAction.Result.Contract.toDomain(): com.procurement.orchestrator.domain.model.contract.Contract {
    return com.procurement.orchestrator.domain.model.contract.Contract(
        id = id,
        status = status,
        statusDetails = statusDetails,
        date = date,
        relatedLots = relatedLots.let { relatedLots -> RelatedLots(relatedLots) },
        suppliers = suppliers?.map { supplier ->
            Organization(
                id = supplier.id,
                name = supplier.name
            )
        }
            .orEmpty()
            .let { Organizations(it) },
        awardId = awardId,
        agreedMetrics = agreedMetrics
            ?.map { agreedMetric ->
                AgreedMetric(
                    id = agreedMetric.id,
                    title = agreedMetric.title,
                    observations = agreedMetric.observations
                        .map { observation ->
                            Observation(
                                id = observation.id,
                                relatedRequirementId = observation.relatedRequirementId,
                                unit = observation.unit
                                    ?.let { unit ->
                                        Unit(
                                            id = unit.id,
                                            name = unit.name
                                        )
                                    },
                                period = observation.period
                                    ?.let { period ->
                                        Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                measure = observation.measure
                            )
                        }.let { Observations(it) }
                )
            }
            .orEmpty()
            .let { AgreedMetrics(it) }

    )
}

