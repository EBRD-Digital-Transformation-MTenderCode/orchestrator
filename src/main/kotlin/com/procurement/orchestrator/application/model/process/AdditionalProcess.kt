package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import java.io.Serializable

data class AdditionalProcess(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid?
) : Serializable