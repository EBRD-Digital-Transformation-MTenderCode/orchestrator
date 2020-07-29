package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.orchestrator.infrastructure.bind.date.JsonDateTimeSerializer
import java.time.LocalDateTime

object StartSecondStage {

    class Request {

        class Payload(
            @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
        ) {
            data class Tender(
                @field:JsonProperty("tenderPeriod") @param:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod
            ) {
                data class TenderPeriod(
                    @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                    @JsonSerialize(using = JsonDateTimeSerializer::class)
                    @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
                )
            }
        }
    }
}
