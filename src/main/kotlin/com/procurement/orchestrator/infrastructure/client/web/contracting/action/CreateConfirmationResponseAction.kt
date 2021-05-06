package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponse
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseType
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponses
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.organization.person.PersonTitle
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateConfirmationResponseAction : FunctionalAction<CreateConfirmationResponseAction.Params, CreateConfirmationResponseAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createConfirmationResponse"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("confirmationResponses") @field:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>
        ) {
            data class ConfirmationResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ConfirmationResponseId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("type") @field:JsonProperty("type") val type: ConfirmationResponseType?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedPerson") @field:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson?
            ) {
                data class RelatedPerson(
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
    }

    data class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) : Serializable {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("confirmationResponses") @field:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>
        ) : Serializable {
            data class ConfirmationResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ConfirmationResponseId,
                @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
                @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: String,
                @param:JsonProperty("type") @field:JsonProperty("type") val type: ConfirmationResponseType,
                @param:JsonProperty("value") @field:JsonProperty("value") val value: String,
                @param:JsonProperty("relatedPerson") @field:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson
            ) : Serializable {
                data class RelatedPerson(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: PersonTitle,
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
}

fun CreateConfirmationResponseAction.Result.Contract.toDomain() =
    Contract(
        id = id,
        confirmationResponses = confirmationResponses.map { confirmationResponse ->
            ConfirmationResponse(
                id = confirmationResponse.id,
                requestId = confirmationResponse.requestId,
                value = confirmationResponse.value,
                type = confirmationResponse.type,
                relatedPerson = confirmationResponse.relatedPerson.let { person ->
                    Person(
                        id = person.id,
                        identifier = person.identifier
                            .let { identifier ->
                                Identifier(
                                    scheme = identifier.scheme,
                                    id = identifier.id,
                                    uri = identifier.uri
                                )
                            },
                        name = person.name,
                        title = person.title.key,
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
                                        }
                                        .orEmpty()
                                        .let { Documents(it) }
                                )
                            }
                            .let { BusinessFunctions(it) }
                    )
                }
            )

        }.let { ConfirmationResponses(it) }
    )

