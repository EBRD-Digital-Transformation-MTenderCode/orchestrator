package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class SetUnsuccessfulStateForLotsAction : FunctionalAction<SetUnsuccessfulStateForLotsAction.Params, SetUnsuccessfulStateForLotsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setUnsuccessfulStateForLots"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {

        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) {

            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
            )

        }
    }

    class Result(
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) : Serializable {

        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) : Serializable {

            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,
                @field:JsonProperty("status") @param:JsonProperty("status") val status: LotStatus,
                @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: LotStatusDetails
            ) : Serializable
        }
    }
}
