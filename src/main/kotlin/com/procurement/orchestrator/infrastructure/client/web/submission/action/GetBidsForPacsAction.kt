package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class GetBidsForPacsAction : FunctionalAction<GetBidsForPacsAction.Params, GetBidsForPacsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getBidsForPacs"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) {
            class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            )
        }
    }

    class Result(
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) : Serializable {
        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) : Serializable {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,
                @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
            ) : Serializable {
                data class Tenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                ) : Serializable

                data class RequirementResponse(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,
                    @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                ) : Serializable {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                    ) : Serializable

                    data class Period(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                    ) : Serializable
                }
            }
        }
    }
}

fun GetBidsForPacsAction.Result.Bids.Detail.toDomain(): Bid {
    return Bid(
        id = id,
        requirementResponses = requirementResponses
            ?.map { requirementResponse ->
                RequirementResponse(
                    id = requirementResponse.id,
                    value = requirementResponse.value,
                    requirement = RequirementReference(
                        id = requirementResponse.requirement.id
                    ),
                    period = requirementResponse.period
                        ?.let { period ->
                            Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        }
                )
            }
            .orEmpty()
            .let { RequirementResponses(it) },
        tenderers = tenderers.map { tenderer ->
            Organization(
                id = tenderer.id,
                name = tenderer.name
            )

        }.let { Organizations(it) }
    )
}

