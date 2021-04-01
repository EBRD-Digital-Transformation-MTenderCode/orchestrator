package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModality
import com.procurement.orchestrator.domain.model.tender.auction.AuctionId
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateRfqDataAction : ProceduralAction<ValidateRfqDataAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateRfqData"

    class Params(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("relatedCpid") @field:JsonProperty("relatedCpid") val relatedCpid: Cpid,
        @param:JsonProperty("relatedOcid") @field:JsonProperty("relatedOcid") val relatedOcid: Ocid
    ) {
        data class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("electronicAuctions") @field:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("procurementMethodModalities") @field:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>?
        ) {
            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("contractPeriod") @field:JsonProperty("contractPeriod") val contractPeriod: ContractPeriod?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("placeOfPerformance") @field:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance?
            ) {
                data class Value(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
                )

                data class ContractPeriod(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                )

                data class PlaceOfPerformance(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?
                ) {
                    data class Address(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails?
                    ) {
                        data class AddressDetails(
                            @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                            @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                            @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                        ) {
                            data class Country(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                            )

                            data class Region(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                            )

                            data class Locality(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                            )
                        }
                    }
                }
            }

            data class Item(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("quantity") @field:JsonProperty("quantity") val quantity: Quantity?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId?
            ) {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                )

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                )
            }

            data class TenderPeriod(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
            )

            data class ElectronicAuctions(
                @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
            ) {
                data class Detail(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: AuctionId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId?
                )
            }
        }
    }
}
