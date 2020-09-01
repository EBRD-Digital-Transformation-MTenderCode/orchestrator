package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OldProcessContext(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") var cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") var ocid: String,
    @field:JsonProperty("operationId") @param:JsonProperty("operationId") var operationId: String,
    @field:JsonProperty("requestId") @param:JsonProperty("requestId") var requestId: String,
    @field:JsonProperty("owner") @param:JsonProperty("owner") var owner: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("stage") @param:JsonProperty("stage") var stage: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("prevStage") @param:JsonProperty("prevStage") var prevStage: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("phase") @param:JsonProperty("phase") var phase: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("processType") @param:JsonProperty("processType") var processType: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") var operationType: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("country") @param:JsonProperty("country") var country: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("language") @param:JsonProperty("language") var language: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("pmd") @param:JsonProperty("pmd") var pmd: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("startDate") @param:JsonProperty("startDate") var startDate: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("timeStamp") @param:JsonProperty("timeStamp") var timestamp: Long?,

    @get:JsonProperty("isAuction") @param:JsonProperty("isAuction") var isAuction: Boolean = false,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") var mainProcurementCategory: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") var awardCriteria: String?
)