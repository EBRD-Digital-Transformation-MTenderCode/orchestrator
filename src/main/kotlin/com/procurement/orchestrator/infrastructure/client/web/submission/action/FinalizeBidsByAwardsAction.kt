package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.award.AwardStatus
import com.procurement.orchestrator.domain.model.award.AwardStatusDetails
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.bid.BidStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FinalizeBidsByAwardsAction.Params
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FinalizeBidsByAwardsAction.Result
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FinalizeBidsByAwardsAction : FunctionalAction<Params, Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "finalizeBidsByAwards"
    override val target: Target<Result> = Target.single()

    class Params(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid?,

        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
    ) {
        data class Award(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardStatus?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatedBid") @field:JsonProperty("relatedBid") val relatedBid: String?
        )
    }

    class Result(
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) : Serializable {

        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) : Serializable {

            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,
                @param:JsonProperty("status") @field:JsonProperty("status") val status: BidStatus
            ) : Serializable
        }
    }
}

fun Result.Bids.Detail.toDomain(): Bid = Bid(id = id, status = status)