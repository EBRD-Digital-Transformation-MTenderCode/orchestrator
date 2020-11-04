package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateBidDataAction : ProceduralAction<ValidateBidDataAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateBidData"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

                @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,
                @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>?
            ) {
                data class Value(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                )

                data class Tenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("details") @field:JsonProperty("details") val details: Details?
                ) {
                    data class Identifier(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String?,

                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

                    data class AdditionalIdentifier(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String?,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

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

                    data class ContactPoint(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("email") @field:JsonProperty("email") val email: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
                    )

                    data class Persone(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                        @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                    ) {
                        data class Identifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        )

                        data class BusinessFunction(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                            @JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                        ) {
                            data class Period(
                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?
                            )

                            data class Document(
                                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                            )
                        }
                    }

                    data class Details(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale?,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
                    ) {
                        data class MainEconomicActivity(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        )

                        data class Permit(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails?
                        ) {
                            data class PermitDetails(
                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy?,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought?,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod?
                            ) {
                                data class IssuedBy(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                                )

                                data class IssuedThought(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                                )

                                data class ValidityPeriod(
                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                                    @JsonInclude(JsonInclude.Include.NON_NULL)
                                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                                )
                            }
                        }

                        data class BankAccount(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification?,

                            @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
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

                            data class Identifier(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                            )

                            data class AccountIdentification(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                            )

                            data class AdditionalAccountIdentifier(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                            )
                        }

                        data class LegalForm(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        )
                    }
                }

                data class Document(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>?,

                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType
                )

                data class Item(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?
                ) {
                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?
                    ) {
                        data class Value(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                        )
                    }
                }
            }
        }

        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("procurementMethodModalities") @field:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String>?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>?
        ) {
            data class Value(
                @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
            )

            data class Item(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?
            ){
                data class Unit(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String?
                )
            }
        }
    }
}