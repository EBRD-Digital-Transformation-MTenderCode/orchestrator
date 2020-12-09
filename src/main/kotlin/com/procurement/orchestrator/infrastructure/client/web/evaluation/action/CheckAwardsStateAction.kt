package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckAwardsStateAction : ProceduralAction<CheckAwardsStateAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkAwardsState"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
        @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: OperationTypeProcess,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender?
    ) {
        data class Award(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId
        )

        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            )
        }
    }
}
