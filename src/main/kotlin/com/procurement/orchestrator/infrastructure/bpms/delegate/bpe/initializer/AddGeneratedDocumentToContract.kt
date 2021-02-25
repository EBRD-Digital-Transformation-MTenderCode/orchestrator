package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

object AddGeneratedDocumentToContract {

    class Request {

        class Payload(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
        ) {
            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )
        }
    }
}