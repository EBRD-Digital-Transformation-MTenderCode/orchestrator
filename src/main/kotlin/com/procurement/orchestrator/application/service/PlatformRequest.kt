package com.procurement.orchestrator.application.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid

class PlatformRequest(
    val operationId: OperationId,
    val platformId: PlatformId,
    val context: Context,
    val payload: String
) {

    class Context(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token? = null,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String? = null,
        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String,
        @field:JsonProperty("processName") @param:JsonProperty("processName") val processName: String,
        @field:JsonProperty("relatedProcess") @param:JsonProperty("relatedProcess") val relatedProcess: RelatedProcess? = null
    ) {
        data class RelatedProcess(
            @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid?
        )
    }
}
