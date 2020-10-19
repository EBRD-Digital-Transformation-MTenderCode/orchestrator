package com.procurement.orchestrator.infrastructure.client.web.clarification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateEnquiryPeriodAction : FunctionalAction<CreateEnquiryPeriodAction.Params, CreateEnquiryPeriodAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createEnquiryPeriod"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
        @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
        @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: OperationTypeProcess
    ) {
        data class Tender(
            @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod
        ) {
            data class TenderPeriod(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
            )
        }
    }

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        data class Tender(
            @param:JsonProperty("enquiryPeriod") @field:JsonProperty("enquiryPeriod") val enquiryPeriod: EnquiryPeriod
        ) : Serializable {
            data class EnquiryPeriod(
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
            ) : Serializable
        }
    }
}