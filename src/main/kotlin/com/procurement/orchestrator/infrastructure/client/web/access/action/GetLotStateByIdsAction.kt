package com.procurement.orchestrator.infrastructure.client.web.access.action

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

abstract class GetLotStateByIdsAction : FunctionalAction<GetLotStateByIdsAction.Params, GetLotStateByIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getLotStateByIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("lotIds") @param:JsonProperty("lotIds") val ids: List<LotId.Permanent>
    )

    class Result(values: List<Lot>) : List<Result.Lot> by values, Serializable {

        class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId.Permanent,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: LotStatus,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: LotStatusDetails
        ) : Serializable
    }
}
