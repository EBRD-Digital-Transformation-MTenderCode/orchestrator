package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.address.Address
import com.procurement.orchestrator.domain.model.address.AddressDetails
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.bid.BidStatus
import com.procurement.orchestrator.domain.model.bid.BidStatusDetails
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.identifier.Identifiers
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.ItemId
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.domain.model.organization.ContactPoint
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.domain.model.organization.datail.Details
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivities
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivity
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifiers
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccount
import com.procurement.orchestrator.domain.model.organization.datail.account.BankAccounts
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalForm
import com.procurement.orchestrator.domain.model.organization.datail.permit.Issue
import com.procurement.orchestrator.domain.model.organization.datail.permit.Permit
import com.procurement.orchestrator.domain.model.organization.datail.permit.PermitDetails
import com.procurement.orchestrator.domain.model.organization.datail.permit.Permits
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.person.Persons
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.domain.model.value.Value
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateBidAction : FunctionalAction<CreateBidAction.Params, CreateBidAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createBid"
    override val target: Target<Result> = Target.single()

    class Params(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid?,

        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
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
                @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
            ) {
                data class Value(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
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

                            @JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
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
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?
                    ) {
                        data class Value(
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount?,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String?
                        )
                    }
                }

                data class RequirementResponse(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                ) {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )

                    data class Period(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                    )
                }
            }
        }
    }

    class Result(
        @param:JsonProperty("token") @field:JsonProperty("token") val token: Token,
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) : Serializable {
        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) : Serializable {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,
                @param:JsonProperty("status") @field:JsonProperty("status") val status: BidStatus,
                @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: BidStatusDetails,
                @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

                @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,
                @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("items") @field:JsonProperty("items") val items: List<Item>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
            ) : Serializable {
                data class Value(
                    @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount,
                    @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                ) : Serializable

                data class Tenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
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
                ) : Serializable {
                    data class Identifier(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    ) : Serializable

                    data class AdditionalIdentifier(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    ) : Serializable

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

                    data class ContactPoint(
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                        @param:JsonProperty("email") @field:JsonProperty("email") val email: String,
                        @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
                    ) : Serializable

                    data class Persone(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                        @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                    ) : Serializable {
                        data class Identifier(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        ) : Serializable

                        data class BusinessFunction(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                            @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                        ) : Serializable {
                            data class Period(
                                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                            ) : Serializable

                            data class Document(
                                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                            ) : Serializable
                        }
                    }

                    data class Details(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,

                        @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: Scale,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

                        @JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
                    ) : Serializable {
                        data class MainEconomicActivity(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        ) : Serializable

                        data class Permit(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                            @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
                        ) : Serializable {
                            data class PermitDetails(
                                @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                                @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                                @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                            ) : Serializable {
                                data class IssuedBy(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                                ) : Serializable

                                data class IssuedThought(
                                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                                ) : Serializable

                                data class ValidityPeriod(
                                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                                ) : Serializable
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

                            data class Identifier(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                            ) : Serializable

                            data class AccountIdentification(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                            ) : Serializable

                            data class AdditionalAccountIdentifier(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                            ) : Serializable
                        }

                        data class LegalForm(
                            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                        ) : Serializable
                    }
                }

                data class Document(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>?,

                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType
                ) : Serializable

                data class Item(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,
                    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit
                ) : Serializable {
                    data class Unit(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value
                    ) : Serializable {
                        data class Value(
                            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount,
                            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
                        ) : Serializable
                    }
                }

                data class RequirementResponse(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,
                    @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,
                    @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
                ) : Serializable {
                    data class Requirement(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    ) : Serializable

                    data class Period(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                    ) : Serializable
                }
            }
        }
    }
}

fun CreateBidAction.Result.Bids.convertToDomainObject() : Bids =
    details.map { bid ->
            Bid(
                id = bid.id,
                status = bid.status,
                statusDetails = bid.statusDetails,
                date = bid.date,
                value = bid.value
                    ?.let { value ->
                        Value(
                            amount = value.amount,
                            currency = value.currency
                        )
                    },
                requirementResponses = bid.requirementResponses
                    ?.map { requirementResponse ->
                        RequirementResponse(
                            id = requirementResponse.id,
                            value = requirementResponse.value,
                            requirement = RequirementReference(requirementResponse.requirement.id),
                            period = requirementResponse.period
                                ?.let { period ->
                                    Period(
                                        startDate = period.startDate,
                                        endDate = period.endDate
                                    )
                                }
                        )
                    }.orEmpty()
                    .let { RequirementResponses(it) },
                tenderers = bid.tenderers
                    .map { tenderer ->
                        Organization(
                            id = tenderer.id,
                            name = tenderer.name,
                            identifier = tenderer.identifier
                                .let { identifier ->
                                    Identifier(
                                        id = identifier.id,
                                        legalName = identifier.legalName,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                            additionalIdentifiers = tenderer.additionalIdentifiers
                                ?.map { additionalIdentifier ->
                                    Identifier(
                                        id = additionalIdentifier.id,
                                        legalName = additionalIdentifier.legalName,
                                        scheme = additionalIdentifier.scheme,
                                        uri = additionalIdentifier.uri
                                    )
                                }.orEmpty()
                                .let { Identifiers(it) },
                            address = tenderer.address
                                ?.let { address ->
                                    Address(
                                        streetAddress = address.streetAddress,
                                        postalCode = address.postalCode,
                                        addressDetails = address.addressDetails
                                            .let { addressDetails ->
                                                AddressDetails(
                                                    country = addressDetails.country
                                                        .let { country ->
                                                            CountryDetails(
                                                                scheme = country.scheme,
                                                                id = country.id,
                                                                description = country.description,
                                                                uri = country.uri
                                                            )
                                                        },
                                                    region = addressDetails.region
                                                        .let { region ->
                                                            RegionDetails(
                                                                scheme = region.scheme,
                                                                id = region.id,
                                                                description = region.description,
                                                                uri = region.uri
                                                            )
                                                        },
                                                    locality = addressDetails.locality
                                                        .let { locality ->
                                                            LocalityDetails(
                                                                scheme = locality.scheme,
                                                                id = locality.id,
                                                                description = locality.description,
                                                                uri = locality.uri
                                                            )
                                                        }
                                                )
                                            }
                                    )
                                },
                            contactPoint = tenderer.contactPoint
                                .let { contactPoint ->
                                    ContactPoint(
                                        name = contactPoint.name,
                                        email = contactPoint.email,
                                        telephone = contactPoint.telephone,
                                        faxNumber = contactPoint.faxNumber,
                                        url = contactPoint.url
                                    )
                                },
                            persons = tenderer.persones
                                ?.map { person ->
                                    Person(
                                        id = PersonId.generate(
                                            scheme = person.identifier.scheme,
                                            id = person.identifier.id
                                        ),
                                        identifier = person.identifier
                                            .let { identifier ->
                                                Identifier(
                                                    scheme = identifier.scheme,
                                                    id = identifier.id,
                                                    uri = identifier.uri
                                                )
                                            },
                                        name = person.name,
                                        title = person.title,
                                        businessFunctions = person.businessFunctions
                                            .map { businessFunction ->
                                                BusinessFunction(
                                                    id = businessFunction.id,
                                                    type = businessFunction.type,
                                                    jobTitle = businessFunction.jobTitle,
                                                    period = Period(startDate = businessFunction.period.startDate),
                                                    documents = businessFunction.documents
                                                        ?.map { document ->
                                                            Document(
                                                                documentType = document.documentType,
                                                                id = document.id,
                                                                title = document.title,
                                                                description = document.description
                                                            )
                                                        }.orEmpty()
                                                        .let { Documents(it) }
                                                )
                                            }.let { BusinessFunctions(it) }
                                    )
                                }.orEmpty()
                                .let { Persons(it) },
                            details = tenderer.details
                                .let { detail ->
                                    Details(
                                        typeOfSupplier = detail.typeOfSupplier,
                                        mainEconomicActivities = detail.mainEconomicActivities
                                            ?.map { mainEconomicActivity ->
                                                MainEconomicActivity(
                                                    id = mainEconomicActivity.id,
                                                    scheme = mainEconomicActivity.scheme,
                                                    uri = mainEconomicActivity.uri,
                                                    description = mainEconomicActivity.description
                                                )
                                            }.orEmpty()
                                            .let { MainEconomicActivities(it) },
                                        scale = detail.scale,
                                        permits = detail.permits?.map { permit ->
                                            Permit(
                                                scheme = permit.scheme,
                                                id = permit.id,
                                                url = permit.url,
                                                permitDetails = permit.permitDetails
                                                    .let { permitDetails ->
                                                        PermitDetails(
                                                            issuedBy = permitDetails.issuedBy
                                                                .let { issuedBy ->
                                                                    Issue(
                                                                        id = issuedBy.id,
                                                                        name = issuedBy.name
                                                                    )
                                                                },
                                                            issuedThought = permitDetails.issuedThought
                                                                .let { issuedThought ->
                                                                    Issue(
                                                                        id = issuedThought.id,
                                                                        name = issuedThought.name
                                                                    )
                                                                },
                                                            validityPeriod = permitDetails.validityPeriod
                                                                .let { validityPeriod ->
                                                                    Period(
                                                                        startDate = validityPeriod.startDate,
                                                                        endDate = validityPeriod.startDate
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        }
                                            .orEmpty()
                                            .let { Permits(it) },
                                        bankAccounts = detail.bankAccounts
                                            ?.map { bankAccount ->
                                                BankAccount(
                                                    description = bankAccount.description,
                                                    bankName = bankAccount.bankName,
                                                    address = bankAccount.address
                                                        .let { address ->
                                                            Address(
                                                                streetAddress = address.streetAddress,
                                                                postalCode = address.postalCode,
                                                                addressDetails = address.addressDetails
                                                                    .let { addressDetails ->
                                                                        AddressDetails(
                                                                            country = addressDetails.country
                                                                                .let { country ->
                                                                                    CountryDetails(
                                                                                        scheme = country.scheme,
                                                                                        id = country.id,
                                                                                        description = country.description,
                                                                                        uri = country.uri
                                                                                    )
                                                                                },
                                                                            region = addressDetails.region
                                                                                .let { region ->
                                                                                    RegionDetails(
                                                                                        scheme = region.scheme,
                                                                                        id = region.id,
                                                                                        description = region.description,
                                                                                        uri = region.uri
                                                                                    )
                                                                                },
                                                                            locality = addressDetails.locality
                                                                                .let { locality ->
                                                                                    LocalityDetails(
                                                                                        scheme = locality.scheme,
                                                                                        id = locality.id,
                                                                                        description = locality.description,
                                                                                        uri = locality.uri
                                                                                    )
                                                                                }
                                                                        )
                                                                    }
                                                            )
                                                        },
                                                    identifier = bankAccount.identifier
                                                        .let { accountIdentifier ->
                                                            AccountIdentifier(
                                                                scheme = accountIdentifier.scheme,
                                                                id = accountIdentifier.id
                                                            )
                                                        },
                                                    accountIdentification = bankAccount.accountIdentification
                                                        .let { accountIdentifier ->
                                                            AccountIdentifier(
                                                                scheme = accountIdentifier.scheme,
                                                                id = accountIdentifier.id
                                                            )

                                                        },
                                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                        ?.map { accountIdentifier ->
                                                            AccountIdentifier(
                                                                scheme = accountIdentifier.scheme,
                                                                id = accountIdentifier.id
                                                            )
                                                        }.orEmpty()
                                                        .let { AccountIdentifiers(it) }
                                                )
                                            }.orEmpty()
                                            .let { BankAccounts(it) },
                                        legalForm = detail.legalForm
                                            ?.let { legalForm ->
                                                LegalForm(
                                                    scheme = legalForm.scheme,
                                                    id = legalForm.id,
                                                    description = legalForm.description,
                                                    uri = legalForm.uri
                                                )
                                            }
                                    )
                                }
                        )
                    }.let { Organizations() },
                relatedLots = RelatedLots(bid.relatedLots),
                documents = bid.documents
                    ?.map { document ->
                        Document(
                            documentType = document.documentType,
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            relatedLots = RelatedLots(document.relatedLots.orEmpty())
                        )
                    }
                    .orEmpty()
                    .let { Documents(it) },
                items = bid.items
                    ?.map { item ->
                        Item(
                            id = item.id,
                            unit = item.unit.let { unit ->
                                Unit(
                                    id = unit.id,
                                    name = unit.name,
                                    value = unit.value
                                        .let { value ->
                                            Value(
                                                amount = value.amount,
                                                currency = value.currency
                                            )
                                        }
                                )
                            }
                        )
                    }.orEmpty()
                    .let { Items(it) }
            )
        }.let { Bids(details = BidsDetails(it)) }