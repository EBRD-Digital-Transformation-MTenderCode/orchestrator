package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class SetStateForLotsAction : FunctionalAction<SetStateForLotsAction.Params, SetStateForLotsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setStateForLots"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
        @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
        @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: OperationTypeProcess
    ) {
        data class Tender(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            )
        }
    }

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        data class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) : Serializable {
            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId,
                @param:JsonProperty("status") @field:JsonProperty("status") val status: LotStatus,
                @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: LotStatusDetails
            ) : Serializable
        }
    }
}

fun SetStateForLotsAction.Result.Tender.Lot.toDomain() = Lot(
    id = id,
    status = status,
    statusDetails = statusDetails
)