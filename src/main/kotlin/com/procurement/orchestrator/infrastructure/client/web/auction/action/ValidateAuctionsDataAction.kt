package com.procurement.orchestrator.infrastructure.client.web.auction.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class ValidateAuctionsDataAction : ProceduralAction<ValidateAuctionsDataAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateAuctionsData"

    class Params(
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @param:JsonProperty("electronicAuctions") @field:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions,
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?
        ) {
            data class ElectronicAuctions(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>?
            ) {
                data class Detail(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId?,

                    @param:JsonProperty("electronicAuctionModalities") @field:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: List<ElectronicAuctionModality>
                ) {
                    data class ElectronicAuctionModality(
                        @param:JsonProperty("eligibleMinimumDifference") @field:JsonProperty("eligibleMinimumDifference") val eligibleMinimumDifference: EligibleMinimumDifference
                    ) {
                        data class EligibleMinimumDifference(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
                        )
                    }
                }
            }

            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?
            )

            data class Value(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
            )
        }
    }
}