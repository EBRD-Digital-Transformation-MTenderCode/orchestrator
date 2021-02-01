package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.evidence.EvidenceId
import java.time.LocalDateTime

object SubmitBidInPcr {

    class Request {

        class Payload(
            @param:JsonProperty("bid") @field:JsonProperty("bid") val bid: Bid
        ) {
            data class Bid(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?,

                @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,
                @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>?
            ) {
                data class Value(
                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String,
                    @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount
                )

                data class RequirementResponse(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("relatedTenderer") @field:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("evidences") @param:JsonProperty("evidences") val evidences: List<Evidence>?,

                    @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                ) {
                    data class RelatedTenderer(
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier
                    ) {
                        data class Identifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                        )
                    }

                    data class Evidence(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: EvidenceId,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("relatedDocument") @field:JsonProperty("relatedDocument") val relatedDocument: RelatedDocument?
                    ) {
                        data class RelatedDocument(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId
                        )
                    }

                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )

                    data class Period(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                    )
                }

                data class Tenderer(
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?,

                    @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>?,

                    @param:JsonProperty("details") @field:JsonProperty("details") val details: Details
                ) {
                    data class Identifier(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

                    data class AdditionalIdentifier(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )

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

                    data class ContactPoint(
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                        @param:JsonProperty("email") @field:JsonProperty("email") val email: String,
                        @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
                    )

                    data class Persone(
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                        @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                    ) {
                        data class Identifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        )

                        data class BusinessFunction(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: BusinessFunctionId,
                            @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                            @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                            @JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                        ) {
                            data class Period(
                                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
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

                        @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale,

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
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        )

                        data class Permit(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("url") @field:JsonProperty("url") val url: String,
                            @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
                        ) {
                            data class PermitDetails(
                                @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                                @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                                @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                            ) {
                                data class IssuedBy(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                                )

                                data class IssuedThought(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                                )

                                data class ValidityPeriod(
                                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                                )
                            }
                        }

                        data class BankAccount(
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                            @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                            @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                            @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                            @JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
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
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

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
                    @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>?,

                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType
                )

                data class Item(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit
                ) {
                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value
                    ) {
                        data class Value(
                            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount,
                            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                        )
                    }
                }
            }
        }
    }
}