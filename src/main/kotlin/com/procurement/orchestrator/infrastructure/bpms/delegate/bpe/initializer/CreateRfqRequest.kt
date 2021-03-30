package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModality
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantityDeserializer
import com.procurement.orchestrator.infrastructure.bind.measure.quantity.QuantitySerializer
import java.time.LocalDateTime

object CreateRfqRequest {

    class Request {

        class Payload(
            @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
        ) {

            data class Tender(
                @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,
                @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>,
                @field:JsonProperty("tenderPeriod") @param:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("electronicAuctions") @param:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>?

            ) {

                data class Classification(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                )

                data class Lot(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

                    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
                    @param:JsonProperty("contractPeriod") @field:JsonProperty("contractPeriod") val contractPeriod: ContractPeriod,
                    @param:JsonProperty("placeOfPerformance") @field:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance

                ) {

                    data class PlaceOfPerformance(
                        @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    ) {

                        data class Address(
                            @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                            @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                        ) {
                            data class AddressDetails(
                                @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                                @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                                @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                            ) {
                                data class Country(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                )

                                data class Region(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                )

                                data class Locality(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String
                                )
                            }
                        }
                    }

                    data class ContractPeriod(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                    )

                    data class Value(
                        @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                    )

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
                                @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: Amount,
                                @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
                            )
                        }
                    }
                }
            }
        }
    }
}
