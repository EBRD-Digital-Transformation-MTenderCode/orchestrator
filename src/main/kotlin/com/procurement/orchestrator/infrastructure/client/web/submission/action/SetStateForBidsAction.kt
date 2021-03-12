package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.bid.BidStatus
import com.procurement.orchestrator.domain.model.bid.BidStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetStateForBidsAction.Params
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetStateForBidsAction.Result
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class SetStateForBidsAction : FunctionalAction<Params, Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setStateForBids"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,

        @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: Bids
    ) {
        data class Bids(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
        ) {
            data class Detail(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: BidId
            )
        }
    }

    class Result(
        @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: Bids
    ) : Serializable {
        data class Bids(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
        ) : Serializable {
            data class Detail(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: BidId,
                @field:JsonProperty("status") @param:JsonProperty("status") val status: BidStatus,
                @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: BidStatusDetails
            ) : Serializable
        }
    }
}

fun Result.Bids.Detail.toDomain(): Bid =
    Bid(
        id = this.id,
        status = this.status,
        statusDetails = this.statusDetails
    )