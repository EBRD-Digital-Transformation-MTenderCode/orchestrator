package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class GetRelatedAwardIdByCansAction :
    FunctionalAction<GetRelatedAwardIdByCansAction.Params, GetRelatedAwardIdByCansAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getCanByIds"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId
        )
    }

    class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
            @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: AwardId,
            @param:JsonProperty("lotId") @field:JsonProperty("lotId") val lotId: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
        ) {
            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>?
            )
        }
    }
}