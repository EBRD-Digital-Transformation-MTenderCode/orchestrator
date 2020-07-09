package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

object DoQualification {

    class Request {

        class Payload(

            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()

        ) {

            data class Document(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
            )
        }
    }
}
