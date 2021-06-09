package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.ProcurementMethod
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.classification.Classifications
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.lot.PlaceOfPerformance
import com.procurement.orchestrator.domain.model.measure.Quantity
import com.procurement.orchestrator.domain.model.tender.AdditionalProcurementCategories
import com.procurement.orchestrator.domain.model.tender.ProcurementCategory
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version

abstract class GetDataForContractAction :
    FunctionalAction<GetDataForContractAction.Params, GetDataForContractAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getDataForContract"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("relatedCpid") @field:JsonProperty("relatedCpid") val relatedCpid: Cpid,
        @param:JsonProperty("relatedOcid") @field:JsonProperty("relatedOcid") val relatedOcid: Ocid,
        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
    ) {
        data class Award(
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>
        )
    }

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,
            @param:JsonProperty("mainProcurementCategory") @field:JsonProperty("mainProcurementCategory") val mainProcurementCategory: ProcurementCategory,
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>,
            @param:JsonProperty("procurementMethod") @field:JsonProperty("procurementMethod") val procurementMethod: ProcurementMethod,
            @param:JsonProperty("procurementMethodDetails") @field:JsonProperty("procurementMethodDetails") val procurementMethodDetails: ProcurementMethodDetails,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("additionalProcurementCategories") @field:JsonProperty("additionalProcurementCategories") val additionalProcurementCategories: List<ProcurementCategory>?
        ) {
            data class Classification(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String
            )

            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String?,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
                @param:JsonProperty("placeOfPerformance") @field:JsonProperty("placeOfPerformance") val placeOfPerformance: PlaceOfPerformance
            ) {
                data class PlaceOfPerformance(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address
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
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            )

                            data class Region(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            )

                            data class Locality(
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
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
                @param:JsonProperty("classification") @field:JsonProperty("classification") val classification: Classification,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("additionalClassifications") @field:JsonProperty("additionalClassifications") val additionalClassifications: List<AdditionalClassification>?,
                @param:JsonProperty("quantity") @field:JsonProperty("quantity") val quantity: Quantity,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                @param:JsonProperty("relatedLot") @field:JsonProperty("relatedLot") val relatedLot: LotId
            ) {
                data class Classification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String
                )

                data class AdditionalClassification(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String
                )

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                )
            }
        }
    }
}

fun GetDataForContractAction.Result.toTenderObject(): Tender =
    tender.let { tender ->
        Tender(
            classification = tender.classification.let { classification ->
                Classification(
                    description = classification.description,
                    id = classification.id,
                    scheme = classification.scheme
                )
            },
            mainProcurementCategory = tender.mainProcurementCategory,
            lots = tender.lots.map { lot ->
                Lot(
                    id = lot.id,
                    description = lot.description,
                    internalId = lot.internalId,
                    title = lot.title,
                    placeOfPerformance = lot.placeOfPerformance.let { placeOfPerformance ->
                        PlaceOfPerformance(
                            address = placeOfPerformance.address.let { address ->
                                Address(
                                    streetAddress = address.streetAddress,
                                    postalCode = address.postalCode,
                                    addressDetails = address.addressDetails.let { addressDetails ->
                                        AddressDetails(
                                            country = addressDetails.country.let { country ->
                                                CountryDetails(
                                                    scheme = country.scheme,
                                                    description = country.description,
                                                    id = country.id
                                                )
                                            },
                                            region = addressDetails.region.let { region ->
                                                RegionDetails(
                                                    scheme = region.scheme,
                                                    description = region.description,
                                                    id = region.id
                                                )
                                            },
                                            locality = addressDetails.locality.let { locality ->
                                                LocalityDetails(
                                                    scheme = locality.scheme,
                                                    description = locality.description,
                                                    id = locality.id
                                                )
                                            }
                                        )
                                    }
                                )
                            },
                            description = placeOfPerformance.description
                        )
                    }
                )
            }
                .let { Lots(it) },
            items = tender.items.map { item ->
                Item(
                    id = item.id,
                    internalId = item.internalId,
                    classification = item.classification.let { classification ->
                        Classification(
                            description = classification.description,
                            id = classification.id,
                            scheme = classification.scheme
                        )
                    },
                    additionalClassifications = item.additionalClassifications?.map { additionalClassification ->
                        Classification(
                            description = additionalClassification.description,
                            id = additionalClassification.id,
                            scheme = additionalClassification.scheme
                        )
                    }
                        .let { Classifications(it.orEmpty()) },
                    quantity = item.quantity,
                    unit = item.unit.let { unit ->
                        Unit(
                            id = unit.id,
                            name = unit.name
                        )
                    },
                    description = item.description,
                    relatedLot = item.relatedLot
                )
            }
                .let { Items(it) },
            procurementMethod = tender.procurementMethod,
            procurementMethodDetails = tender.procurementMethodDetails,
            additionalProcurementCategories = AdditionalProcurementCategories(tender.additionalProcurementCategories.orEmpty())
        )
    }
