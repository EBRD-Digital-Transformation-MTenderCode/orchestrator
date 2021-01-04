package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.measure.Amount

object DivideLotProcess {

    class Request {

        class Payload(
            @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
        ) {
            data class Tender(
                @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,
                @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>
            ) {

                data class Lot(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
                    @field:JsonProperty("contractPeriod") @param:JsonProperty("contractPeriod") val contractPeriod: ContractPeriod,
                    @field:JsonProperty("placeOfPerformance") @param:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance
                ) {

                    data class Value(
                        @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount,
                        @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                    )

                    data class ContractPeriod(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String,
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String
                    )

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
                }

                data class Item(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String
                )

            }
        }
    }
}
