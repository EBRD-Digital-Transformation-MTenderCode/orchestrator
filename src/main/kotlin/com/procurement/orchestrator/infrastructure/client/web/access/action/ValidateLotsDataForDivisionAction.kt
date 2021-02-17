package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateLotsDataForDivisionAction : ProceduralAction<ValidateLotsDataForDivisionAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateLotsDataForDivision"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>
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
                @param:JsonProperty("placeOfPerformance") @field:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance?,

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
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

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
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
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
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("maxExtentDate") @field:JsonProperty("maxExtentDate") val maxExtentDate: LocalDateTime?
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
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?
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
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("maxExtentDate") @field:JsonProperty("maxExtentDate") val maxExtentDate: LocalDateTime?
                    )
                }
            }

            data class Item(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId?
            )
        }
    }
}
