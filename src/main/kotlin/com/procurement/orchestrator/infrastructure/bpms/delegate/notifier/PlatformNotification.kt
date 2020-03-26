package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.ResponseId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import java.time.LocalDateTime

object PlatformNotification {

    class MessageEnvelop(
        @field:JsonProperty("platformId") @param:JsonProperty("platformId") val platformId: PlatformId,
        @field:JsonProperty("operationId") @param:JsonProperty("operationId") val operationId: OperationId,
        @field:JsonProperty("message") @param:JsonProperty("message") val message: String
    )

    class Message(
        @field:JsonProperty("X-RESPONSE-ID") @param:JsonProperty("X-RESPONSE-ID") val responseId: ResponseId,
        @field:JsonProperty("X-OPERATION-ID") @param:JsonProperty("X-OPERATION-ID") val operationId: OperationId,
        @field:JsonProperty("initiator") @param:JsonProperty("initiator") val initiator: Initiator,
        @field:JsonProperty("data") @param:JsonProperty("data") val body: Body
    ) {

        sealed class Body {

            class Success(
                @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
                @field:JsonProperty("url") @param:JsonProperty("url") val url: String,
                @field:JsonProperty("operationDate") @param:JsonProperty("operationDate") val operationDate: LocalDateTime,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("outcomes") @param:JsonProperty("outcomes") val outcomes: Outcomes? = null
            ) : Body()

            class Errors(items: List<Error>) : List<Errors.Error> by items, Body() {

                data class Error(
                    @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String
                )
            }
        }
    }

    class Outcomes(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList()
    ) {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("X-TOKEN") @param:JsonProperty("X-TOKEN") val token: Token? = null
        )
    }
}
