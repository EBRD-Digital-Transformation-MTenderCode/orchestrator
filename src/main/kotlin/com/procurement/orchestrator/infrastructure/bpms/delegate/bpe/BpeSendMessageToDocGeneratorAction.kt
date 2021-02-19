package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import java.time.LocalDateTime

abstract class BpeSendMessageToDocGeneratorAction {

    data class Data(
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("language") @param:JsonProperty("language") val language: String,
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: OperationId,
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("documentInitiator") @param:JsonProperty("documentInitiator") val documentInitiator: OperationTypeProcess
    )
}
