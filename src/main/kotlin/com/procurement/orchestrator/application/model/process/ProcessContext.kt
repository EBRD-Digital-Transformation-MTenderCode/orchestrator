package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class ProcessContext(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") var cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") var ocid: String,
    @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: String,
    @field:JsonProperty("requestId") @param:JsonProperty("requestId") val requestId: String,
    @field:JsonProperty("owner") @param:JsonProperty("owner") var owner: String,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("stage") @param:JsonProperty("stage") val stage: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("prevStage") @param:JsonProperty("prevStage") val prevStage: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("phase") @param:JsonProperty("phase") val phase: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("processDefinitionKey") @param:JsonProperty("processDefinitionKey") val processDefinitionKey: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("country") @param:JsonProperty("country") val country: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("language") @param:JsonProperty("language") val language: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("timeStamp") @param:JsonProperty("timeStamp") val timestamp: Long? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isAuction") @param:JsonProperty("isAuction") val isAuction: Boolean,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") val mainProcurementCategory: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: String? = null
)
