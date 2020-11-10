package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.address.country.CountryId
import com.procurement.orchestrator.domain.model.address.locality.LocalityId
import com.procurement.orchestrator.domain.model.address.region.RegionId
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.organization.datail.MainEconomicActivityId
import com.procurement.orchestrator.domain.model.organization.datail.Scale
import com.procurement.orchestrator.domain.model.organization.datail.TypeOfSupplier
import com.procurement.orchestrator.domain.model.organization.datail.legalform.LegalFormId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.PersonTitle
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.SubmissionStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class GetSubmissionCandidateReferencesByQualificationIdsAction : FunctionalAction<GetSubmissionCandidateReferencesByQualificationIdsAction.Params, GetSubmissionCandidateReferencesByQualificationIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getSubmissionsByQualificationIds"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("qualifications") @param:JsonProperty("qualifications") val qualifications: List<Qualification>
    ) {
        class Qualification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: QualificationId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedSubmission") @param:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId?

        )
    }

    class Result(
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: Submissions
    ) : Serializable {

        class Submissions(
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
        ) : Serializable {

            class Detail(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
                @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
                @field:JsonProperty("status") @param:JsonProperty("status") val status: SubmissionStatus,
                @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: List<Candidate>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
            ) : Serializable {
                class RequirementResponse(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId,
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,
                    @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
                    @field:JsonProperty("relatedCandidate") @param:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate
                ) : Serializable {

                    class Requirement(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
                    ) : Serializable

                    class RelatedCandidate(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    ) : Serializable
                }

                class Candidate(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                    @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>,
                    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                    @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint,
                    @field:JsonProperty("details") @param:JsonProperty("details") val details: Details,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("persones") @param:JsonProperty("persones") val persones: List<Persone>?
                ) : Serializable {

                    class Identifier(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
                        @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                    ) : Serializable

                    class AdditionalIdentifier(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                    ) : Serializable

                    class Address(
                        @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,
                        @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?
                    ) : Serializable {

                        class AddressDetails(
                            @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                            @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                            @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                        ) : Serializable {

                            class Country(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: CountryId,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                            ) : Serializable

                            class Region(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                            ) : Serializable

                            class Locality(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: LocalityId,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                            ) : Serializable
                        }
                    }

                    class ContactPoint(
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                        @field:JsonProperty("email") @param:JsonProperty("email") val email: String,
                        @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String?,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("url") @param:JsonProperty("url") val url: String?
                    ) : Serializable

                    class Persone(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: PersonId,
                        @field:JsonProperty("title") @param:JsonProperty("title") val title: PersonTitle,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                        @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
                    ) : Serializable {

                        class Identifier(
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                        ) : Serializable

                        class BusinessFunction(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: BusinessFunctionId,
                            @field:JsonProperty("type") @param:JsonProperty("type") val type: String,
                            @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String,
                            @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,
                            @JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
                        ) : Serializable {

                            class Period(
                                @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
                            ) : Serializable

                            class Document(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                            ) : Serializable
                        }
                    }

                    class Details(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier?,

                        @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>,
                        @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: Scale,
                        @field:JsonProperty("bankAccounts") @param:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>,
                        @field:JsonProperty("legalForm") @param:JsonProperty("legalForm") val legalForm: LegalForm
                    ) : Serializable {

                        class MainEconomicActivity(
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: MainEconomicActivityId,
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                        ) : Serializable

                        class BankAccount(
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                            @field:JsonProperty("bankName") @param:JsonProperty("bankName") val bankName: String,
                            @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                            @field:JsonProperty("accountIdentification") @param:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                            @JsonInclude(JsonInclude.Include.NON_EMPTY)
                            @field:JsonProperty("additionalAccountIdentifiers") @param:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
                        ) : Serializable {

                            class Address(
                                @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,
                                @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?
                            ) : Serializable {

                                class AddressDetails(
                                    @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                                    @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                                    @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                                ) : Serializable {

                                    class Country(
                                        @field:JsonProperty("id") @param:JsonProperty("id") val id: CountryId,
                                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                                    ) : Serializable

                                    class Region(
                                        @field:JsonProperty("id") @param:JsonProperty("id") val id: RegionId,
                                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                                    ) : Serializable

                                    class Locality(
                                        @field:JsonProperty("id") @param:JsonProperty("id") val id: LocalityId,
                                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
                                    ) : Serializable
                                }
                            }

                            class Identifier(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId
                            ) : Serializable

                            class AccountIdentification(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                            ) : Serializable

                            class AdditionalAccountIdentifier(
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                            ) : Serializable
                        }

                        class LegalForm(
                            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: LegalFormId,
                            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                        ) : Serializable
                    }
                }

                class Document(
                    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                ) : Serializable
            }
        }
    }
}
