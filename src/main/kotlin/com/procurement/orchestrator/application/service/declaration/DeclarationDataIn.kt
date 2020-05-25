package com.procurement.orchestrator.application.service.declaration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer

object DeclarationDataIn {

    class Request(
        val operationId: OperationId,
        val platformId: PlatformId,
        val context: Context,
        val payload: String
    ) {

        class Context(
            @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
            @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
            @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
        )
    }

    class Payload(
        @field:JsonProperty("requirementResponse") @param:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse
    ) {

        data class RequirementResponse(

            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,

            @field:JsonProperty("relatedTenderer") @param:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
            @field:JsonProperty("responder") @param:JsonProperty("responder") val responder: Responder,
            @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement
        ) {

            data class RelatedTenderer(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
            )

            data class Responder(
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
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("type") @param:JsonProperty("type") val type: String,
                    @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String,
                    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
                ) {

                    data class Period(
                        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: String
                    )

                    data class Document(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String,
                        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
                    )
                }
            }

            data class Requirement(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
            )
        }
    }
}
