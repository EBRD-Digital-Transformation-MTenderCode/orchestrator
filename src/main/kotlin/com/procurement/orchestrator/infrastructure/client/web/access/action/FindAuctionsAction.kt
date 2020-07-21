package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindAuctionsAction : FunctionalAction<FindAuctionsAction.Params, FindAuctionsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findAuctions"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        data class Tender(
            @param:JsonProperty("electronicAuctions") @field:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions
        ) : Serializable {
            data class ElectronicAuctions(
                @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
            ) : Serializable {
                data class Detail(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId,
                    @param:JsonProperty("electronicAuctionModalities") @field:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: List<ElectronicAuctionModality>
                ) : Serializable {
                    data class ElectronicAuctionModality(
                        @param:JsonProperty("eligibleMinimumDifference") @field:JsonProperty("eligibleMinimumDifference") val eligibleMinimumDifference: EligibleMinimumDifference
                    ) : Serializable {
                        data class EligibleMinimumDifference(
                            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount,
                            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                        ) : Serializable
                    }
                }
            }
        }
    }
}
