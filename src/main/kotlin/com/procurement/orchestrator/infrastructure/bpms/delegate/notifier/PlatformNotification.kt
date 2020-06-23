package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.ResponseId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.submission.SubmissionId
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
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
                ) {

                    class Detail(
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String?,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
                    )
                }
            }
        }
    }

    class Outcomes(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("amendments") @param:JsonProperty("amendments") val amendments: List<Amendment> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: List<Submission> = emptyList()
    ) {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("X-TOKEN") @param:JsonProperty("X-TOKEN") val token: Token? = null
        )

        data class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId
        )

        data class Submission(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
            @field:JsonProperty("X-TOKEN") @param:JsonProperty("X-TOKEN") val token: Token
        )
    }
}
