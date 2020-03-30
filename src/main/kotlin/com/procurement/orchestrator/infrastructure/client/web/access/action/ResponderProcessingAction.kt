package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionId
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctionType
import com.procurement.orchestrator.infrastructure.client.web.Target
import java.time.LocalDateTime

abstract class ResponderProcessingAction :
    FunctionalAction<ResponderProcessingAction.Params, ResponderProcessingAction.Result> {
    override val version: String = "2.0.0"
    override val name: String = "responderProcessing"
    override val target: Target<Result> = Target.Single(typeRef = Result::class.java)

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
        @field:JsonProperty("responder") @param:JsonProperty("responder") val responder: Responder
    ) {

        class Responder(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
        ) {

            data class Identifier(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
            )

            data class BusinessFunction(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: BusinessFunctionId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("type") @param:JsonProperty("type") val type: BusinessFunctionType?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("period") @param:JsonProperty("period") val period: Period?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
            ) {

                data class Period(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime?
                )

                data class Document(
                    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                )
            }
        }
    }

    class Result(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
        @field:JsonProperty("persons") @param:JsonProperty("persons") val persons: List<Person>
    ) {

        data class Identifier(
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
        )

        data class Person(
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
            @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
        ) {
            data class Identifier(
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
            )

            data class BusinessFunction(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: BusinessFunctionId,
                @field:JsonProperty("type") @param:JsonProperty("type") val type: BusinessFunctionType,
                @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String,
                @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()

            ) {

                data class Period(
                    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
                )

                data class Document(
                    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                )
            }
        }
    }
}
