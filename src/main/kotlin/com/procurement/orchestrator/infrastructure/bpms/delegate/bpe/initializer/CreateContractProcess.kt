package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonProperty

object CreateContractProcess {

    class Request {

        class Payload(
            @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
        ) {
            data class Contract(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )
        }
    }
}
