package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class ValidateTenderPeriodAction : ProceduralAction<ValidateTenderPeriodAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateTenderPeriod"

    class Params(
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
        @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod
        ) {
            data class TenderPeriod(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
            )
        }
    }
}
