package com.procurement.orchestrator.infrastructure.message.chronograph

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.databinding.LocalDateTimeDeserializer
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer
import java.time.LocalDateTime

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

        @JsonSerialize(using = LocalDateTimeSerializer::class)
        @JsonDeserialize(using = LocalDateTimeDeserializer::class)
        @field:JsonProperty("timestamp") @param:JsonProperty("timestamp") val timestamp: LocalDateTime,

        @field:JsonProperty("processType") @param:JsonProperty("processType") val processType: String,
        @field:JsonProperty("requestId") @param:JsonProperty("requestId") val requestId: String,
        @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: String,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
    )
}