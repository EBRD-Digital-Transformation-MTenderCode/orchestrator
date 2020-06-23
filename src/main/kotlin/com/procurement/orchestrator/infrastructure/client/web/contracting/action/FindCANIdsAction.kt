package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.can.CanId
import com.procurement.orchestrator.domain.model.can.CanStatus
import com.procurement.orchestrator.domain.model.can.CanStatusDetails
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindCANIdsAction : FunctionalAction<FindCANIdsAction.Params, FindCANIdsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findCANIds"
    override val target: Target<Result> = Target.plural()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("states") @param:JsonProperty("states") val states: List<State>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("lotIds") @param:JsonProperty("lotIds") val lotIds: List<LotId>?

    ) {
        data class State(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("status") @param:JsonProperty("status") val status: CanStatus? = null,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: CanStatusDetails? = null
        )
    }

    class Result(canIds: List<CanId>) : List<CanId> by canIds, Serializable
}
