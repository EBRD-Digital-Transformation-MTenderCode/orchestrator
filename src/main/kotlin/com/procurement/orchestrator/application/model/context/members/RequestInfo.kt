package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.RequestId
import java.io.Serializable
import java.time.LocalDateTime

/**
 * Information with which the process was started.
 */
data class RequestInfo(
    @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: OperationId,
    @field:JsonProperty("parentOperationId") @param:JsonProperty("parentOperationId") val parentOperationId: OperationId?,
    @field:JsonProperty("timestamp") @param:JsonProperty("timestamp") val timestamp: LocalDateTime,
    @field:JsonProperty("requestId") @param:JsonProperty("requestId") val requestId: RequestId,
    @field:JsonProperty("platformId") @param:JsonProperty("platformId") val platformId: PlatformId,
    @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
    @field:JsonProperty("language") @param:JsonProperty("language") val language: String
) : Serializable
