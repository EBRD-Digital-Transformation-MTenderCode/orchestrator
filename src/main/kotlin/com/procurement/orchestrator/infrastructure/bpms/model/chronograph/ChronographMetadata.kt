package com.procurement.orchestrator.infrastructure.bpms.model.chronograph

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import java.time.LocalDateTime

data class ChronographMetadata(

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: String?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("requestId") @param:JsonProperty("requestId") val requestId: String?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("phase") @param:JsonProperty("phase") val phase: String?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("timeStamp") @param:JsonProperty("timeStamp") val timeStamp: LocalDateTime?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("processType") @param:JsonProperty("processType") val processType: String?,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isAuction") @param:JsonProperty("isAuction") val isAuction: Boolean?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("platformId") @param:JsonProperty("platformId") val platformId: PlatformId?

)