package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

object IssuingFrameworkContract {

    class Request {

        class Payload(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("contract") @field:JsonProperty("contract") val contract: Contract?
        ) {
            data class Contract(
                @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String
            )
        }
    }
}