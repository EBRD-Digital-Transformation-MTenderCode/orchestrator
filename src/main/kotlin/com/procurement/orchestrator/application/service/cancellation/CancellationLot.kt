package com.procurement.orchestrator.application.service.cancellation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.lot.LotId

object CancellationLot {

    class Request(
        val operationId: OperationId,
        val platformId: PlatformId,
        val context: Context,
        val payload: String
    ) {
        class Context(
            @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
            @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
            @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
            @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: LotId
        )
    }

    class Payload(
        @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment>
    ) {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
        ) {

            data class Document(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
            )
        }
    }
}
