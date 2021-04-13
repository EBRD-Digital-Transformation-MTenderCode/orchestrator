package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.RelatedProcessScheme
import com.procurement.orchestrator.domain.model.contract.RelatedProcessType
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.tender.AwardCriteria
import com.procurement.orchestrator.domain.model.tender.AwardCriteriaDetails
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModality
import com.procurement.orchestrator.domain.model.tender.TenderId
import com.procurement.orchestrator.domain.model.tender.TenderStatus
import com.procurement.orchestrator.domain.model.tender.TenderStatusDetails
import com.procurement.orchestrator.domain.model.tender.auction.AuctionId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateRfqAction : FunctionalAction<CreateRfqAction.Params, CreateRfqAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createRfq"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("relatedCpid") @field:JsonProperty("relatedCpid") val relatedCpid: Cpid,
        @param:JsonProperty("relatedOcid") @field:JsonProperty("relatedOcid") val relatedOcid: Ocid,
        @param:JsonProperty("additionalCpid") @field:JsonProperty("additionalCpid") val additionalCpid: Cpid,
        @param:JsonProperty("additionalOcid") @field:JsonProperty("additionalOcid") val additionalOcid: Ocid
    ) {
        data class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>,

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

                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                            )

                            data class Region(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                            )

                            data class Locality(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
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
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                )

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                )
            }

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

    class Result(
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("token") @field:JsonProperty("token") val token: Token,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("relatedProcesses") @field:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcess>
    ) : Serializable {
        data class Tender(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: TenderId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: TenderStatus,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: TenderStatusDetails,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("electronicAuctions") @field:JsonProperty("electronicAuctions") val electronicAuctions: ElectronicAuctions?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("procurementMethodModalities") @field:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String>?,

            @param:JsonProperty("awardCriteria") @field:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria,
            @param:JsonProperty("awardCriteriaDetails") @field:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails,
            @param:JsonProperty("value") @field:JsonProperty("value") val value: Value
        ) : Serializable {

            data class Value(
                @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
            )

            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value,
                @param:JsonProperty("contractPeriod") @field:JsonProperty("contractPeriod") val contractPeriod: ContractPeriod,
                @param:JsonProperty("placeOfPerformance") @field:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance,
                @param:JsonProperty("status") @field:JsonProperty("status") val status: LotStatus,
                @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: LotStatusDetails
            ) : Serializable {
                data class Value(
                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                ) : Serializable

                data class ContractPeriod(
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                ) : Serializable

                data class PlaceOfPerformance(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address
                ) : Serializable {
                    data class Address(
                        @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                        @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                    ) : Serializable {
                        data class AddressDetails(
                            @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                            @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                            @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                        ) : Serializable {
                            data class Country(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            ) : Serializable

                            data class Region(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            ) : Serializable

                            data class Locality(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                            ) : Serializable
                        }
                    }
                }
            }

            data class Item(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,

                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,
                @param:JsonProperty("quantity") @field:JsonProperty("quantity") val quantity: Quantity,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId
            ) : Serializable {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String
                ) : Serializable

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                ) : Serializable
            }

            data class ElectronicAuctions(
                @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
            ) : Serializable {
                data class Detail(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: AuctionId,
                    @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId
                ) : Serializable
            }
        }

        data class RelatedProcess(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("relationship") @field:JsonProperty("relationship") val relationship: List<RelatedProcessType>,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: RelatedProcessScheme,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: String,
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
        ) : Serializable
    }
}
