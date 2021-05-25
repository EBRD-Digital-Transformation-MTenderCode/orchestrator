package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class PersonesProcessingAction :
    FunctionalAction<PersonesProcessingAction.Params, PersonesProcessingAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "personesProcessing"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
    ) {
        data class Party(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>
        ) {
            data class Persone(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String?,
                @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: BusinessFunctionId,
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
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType?,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    )
                }
            }
        }
    }

    class Result(
        @param:JsonProperty("parties") @field:JsonProperty("parties")
        val parties: List<Party>
    ) : Serializable {
        data class Party(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,
            @param:JsonProperty("address") @field:JsonProperty("address") val address: Adress,
            @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,
            @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>
        ) : Serializable {
            data class Identifier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            ) : Serializable

            data class AdditionalIdentifier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            ) : Serializable

            data class Adress(
                @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,
                @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AdressDetails
            ) : Serializable {
                data class AdressDetails(
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
                @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BuisnessFunction>
            ) : Serializable {
                data class Identifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                ) : Serializable

                data class BuisnessFunction(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: BusinessFunctionId,
                    @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                    @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,
                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                ) : Serializable {
                    data class Period(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                    ) : Serializable

                    data class Document(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentType,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    ) : Serializable
                }
            }
        }
    }
}