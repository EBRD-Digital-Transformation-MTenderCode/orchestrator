package com.procurement.orchestrator.infrastructure.client.web.auction.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.service.FunctionalActionV1
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.tender.auction.AuctionId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class ScheduleAuctionsAction : FunctionalActionV1<ScheduleAuctionsAction.Context, ScheduleAuctionsAction.Data, ScheduleAuctionsAction.ResponseData> {
    override val version: Version = Version.parse("0.0.1")
    override val name: String = "scheduleAuctions"
    override val target: Target<ResponseData> = Target.single()

    class Context(
        @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: OperationId,
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails
    )

    class Data(
        @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod,
        @param:JsonProperty("electronicAuctions") @field:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions
    ) {
        data class TenderPeriod(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
        )

        data class ElectronicAuctions(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>?
        ) {
            data class Detail(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId?,

                @param:JsonProperty("electronicAuctionModalities") @field:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: List<ElectronicAuctionModality>
            ) {
                data class ElectronicAuctionModality(
                    @param:JsonProperty("eligibleMinimumDifference") @field:JsonProperty("eligibleMinimumDifference") val eligibleMinimumDifference: EligibleMinimumDifference
                ) {
                    data class EligibleMinimumDifference(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
                    )
                }
            }
        }
    }

    class ResponseData(
        @field:JsonProperty("auctionPeriod") @param:JsonProperty("auctionPeriod") val auctionPeriod: AuctionPeriod,
        @field:JsonProperty("electronicAuctions") @param:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions
    ) : Serializable {
        data class AuctionPeriod(
            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
        ) : Serializable

        data class ElectronicAuctions(
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
        ) : Serializable {
            data class Detail(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: AuctionId,
                @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId,

                @field:JsonProperty("auctionPeriod") @param:JsonProperty("auctionPeriod") val auctionPeriod: AuctionPeriod,
                @field:JsonProperty("electronicAuctionModalities") @param:JsonProperty("electronicAuctionModalities") val electronicAuctionModalities: List<ElectronicAuctionModality>
            ) : Serializable {
                data class AuctionPeriod(
                    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
                ) : Serializable

                data class ElectronicAuctionModality(
                    @field:JsonProperty("url") @param:JsonProperty("url") val url: String,
                    @field:JsonProperty("eligibleMinimumDifference") @param:JsonProperty("eligibleMinimumDifference") val eligibleMinimumDifference: EligibleMinimumDifference
                ) : Serializable {
                    data class EligibleMinimumDifference(
                        @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Amount,
                        @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                    ) : Serializable
                }
            }
        }
    }
}
