package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.databinding.LocalDateTimeDeserializer
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.domain.model.address.country.CountryScheme
import com.procurement.orchestrator.domain.model.address.locality.LocalityId
import com.procurement.orchestrator.domain.model.address.locality.LocalityScheme
import com.procurement.orchestrator.domain.model.address.region.RegionId
import com.procurement.orchestrator.domain.model.address.region.RegionScheme
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.identifier.IdentifierId
import com.procurement.orchestrator.domain.model.identifier.IdentifierScheme
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivityId
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivityScheme
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifierId
import com.procurement.orchestrator.domain.model.organization.datail.account.AccountIdentifierScheme
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalFormId
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalFormScheme
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence.EligibleEvidenceType
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import java.time.LocalDateTime

object CreateSubmission {

    class Request {

        class Payload(
            @field:JsonProperty("submission") @param:JsonProperty("submission") val submission: Submission
        ) {

            data class Submission(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,

                @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse> = emptyList(),

                @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: List<Candidate>,

                @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()

            ) {

                data class RequirementResponse(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId,

                    @JsonDeserialize(using = RequirementValueDeserializer::class)
                    @JsonSerialize(using = RequirementValueSerializer::class)
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,

                    @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
                    @field:JsonProperty("relatedCandidate") @param:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate,

                    @field:JsonProperty("evidences") @param:JsonProperty("evidences") val evidences: List<Evidence>
                ) {

                    data class Requirement(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
                    )

                    data class RelatedCandidate(
                        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    ) {

                        data class Identifier(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                        )
                    }

                    data class Evidence(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
                        @field:JsonProperty("type") @param:JsonProperty("type") val type: EligibleEvidenceType,

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("relatedDocument") @param:JsonProperty("relatedDocument") val relatedDocument: DocumentReference?
                    ) {
                        data class DocumentReference(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId
                        )
                    }
                }

                data class Candidate(
                    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String,

                    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<Identifier> = emptyList(),

                    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                    @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint,

                    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("persones") @param:JsonProperty("persones") val persons: List<Person> = emptyList(),

                    @field:JsonProperty("details") @param:JsonProperty("details") val details: Details
                ) {

                    data class Identifier(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: IdentifierScheme,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: IdentifierId,
                        @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                    )

                    data class Address(
                        @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String? = null,

                        @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
                    ) {

                        data class AddressDetails(
                            @field:JsonProperty("country") @param:JsonProperty("country") val country: CountryDetails,
                            @field:JsonProperty("region") @param:JsonProperty("region") val region: RegionDetails,
                            @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: LocalityDetails
                        ) {

                            data class CountryDetails(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: CountryScheme,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: CountryId,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                            )

                            data class RegionDetails(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RegionScheme,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                            )

                            data class LocalityDetails(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: LocalityScheme,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: LocalityId,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                            )
                        }
                    }

                    data class ContactPoint(
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                        @field:JsonProperty("email") @param:JsonProperty("email") val email: String,
                        @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String,

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String? = null,

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("url") @param:JsonProperty("url") val url: String? = null
                    )

                    data class Person(
                        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                        @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                    ) {

                        data class Identifier(
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: IdentifierScheme,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: IdentifierId,

                            @field:JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                        )

                        data class BusinessFunction(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: BusinessFunctionId,
                            @field:JsonProperty("type") @param:JsonProperty("type") val type: BusinessFunctionType,
                            @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String,
                            @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,

                            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
                        ) {

                            data class Period(
                                @JsonSerialize(using = LocalDateTimeSerializer::class)
                                @JsonDeserialize(using = LocalDateTimeDeserializer::class)
                                @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
                            )

                            data class Document(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                            )
                        }
                    }

                    data class Details(

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier? = null,

                        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity> = emptyList(),

                        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                        @field:JsonProperty("bankAccounts") @param:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount> = emptyList(),

                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("legalForm") @param:JsonProperty("legalForm") val legalForm: LegalForm? = null,

                        @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: Scale
                    ) {

                        data class MainEconomicActivity(
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: MainEconomicActivityScheme,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: MainEconomicActivityId,
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                            @field:JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                        )

                        data class BankAccount(
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                            @field:JsonProperty("bankName") @param:JsonProperty("bankName") val bankName: String,
                            @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: AccountIdentifier,
                            @field:JsonProperty("accountIdentification") @param:JsonProperty("accountIdentification") val accountIdentification: AccountIdentifier,

                            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @field:JsonProperty("additionalAccountIdentifiers") @param:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AccountIdentifier> = emptyList()
                        ) {

                            data class Address(
                                @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String? = null,

                                @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
                            ) {

                                data class AddressDetails(
                                    @field:JsonProperty("country") @param:JsonProperty("country") val country: CountryDetails,
                                    @field:JsonProperty("region") @param:JsonProperty("region") val region: RegionDetails,
                                    @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: LocalityDetails
                                ) {

                                    data class CountryDetails(
                                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: CountryScheme,
                                        @field:JsonProperty("id") @param:JsonProperty("id") val id: CountryId,
                                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                                    )

                                    data class RegionDetails(
                                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RegionScheme,
                                        @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,
                                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                                    )

                                    data class LocalityDetails(
                                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: LocalityScheme,
                                        @field:JsonProperty("id") @param:JsonProperty("id") val id: LocalityId,
                                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                                        @field:JsonInclude(JsonInclude.Include.NON_NULL)
                                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
                                    )
                                }
                            }

                            data class AccountIdentifier(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: AccountIdentifierScheme,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: AccountIdentifierId
                            )
                        }

                        data class LegalForm(
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: LegalFormScheme,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: LegalFormId,
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                            @field:JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                        )
                    }
                }

                data class Document(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @field:JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                )
            }
        }
    }
}
