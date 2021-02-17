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
                    @field:JsonProperty("placeOfPerformance") @param:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("hasOptions") @field:JsonProperty("hasOptions") val hasOptions: Boolean?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("options") @field:JsonProperty("options") val options: List<Option>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("hasRecurrence") @field:JsonProperty("hasRecurrence") val hasRecurrence: Boolean?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("recurrence") @field:JsonProperty("recurrence") val recurrence: Recurrence?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("hasRenewal") @field:JsonProperty("hasRenewal") val hasRenewal: Boolean?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("renewal") @field:JsonProperty("renewal") val renewal: Renewal?
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

                    data class Option(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                    ) {
                        data class Period(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("durationInDays") @field:JsonProperty("durationInDays") val durationInDays: Int?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("maxExtentDate") @field:JsonProperty("maxExtentDate") val maxExtentDate: String?
                        )
                    }

                    data class Recurrence(
                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("dates") @field:JsonProperty("dates") val dates: List<Date>?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    ) {
                        data class Date(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String?
                        )
                    }

                    data class Renewal(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("minimumRenewals") @field:JsonProperty("minimumRenewals") val minimumRenewals: Int?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("maximumRenewals") @field:JsonProperty("maximumRenewals") val maximumRenewals: Int?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                    ) {
                        data class Period(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("durationInDays") @field:JsonProperty("durationInDays") val durationInDays: Int?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("maxExtentDate") @field:JsonProperty("maxExtentDate") val maxExtentDate: String?
                        )
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
