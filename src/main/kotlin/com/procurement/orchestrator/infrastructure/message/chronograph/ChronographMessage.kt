package com.procurement.orchestrator.infrastructure.message.chronograph

import com.fasterxml.jackson.annotation.JsonProperty

class ChronographMessage(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("data") @param:JsonProperty("data") val data: Data
) {

    class Data(
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
        @field:JsonProperty("phase") @param:JsonProperty("phase") val phase: String,
        @field:JsonProperty("launchTime") @param:JsonProperty("launchTime") val launchTime: String,
        @field:JsonProperty("metaData") @param:JsonProperty("metaData") val metadata: String
    )

    class Metadata(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: String,
        @field:JsonProperty("processType") @param:JsonProperty("processType") val processType: String,
        @field:JsonProperty("requestId") @param:JsonProperty("requestId") val requestId: String,
        @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: String,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
    )
}