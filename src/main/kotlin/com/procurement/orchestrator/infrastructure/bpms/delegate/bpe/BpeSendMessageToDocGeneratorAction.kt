package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import java.time.LocalDateTime

abstract class BpeSendMessageToDocGeneratorAction {

    data class Data(
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("language") @param:JsonProperty("language") val language: String,
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: OperationId,
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>?
    ) {
        data class Contract(
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String
        )
    }
}
