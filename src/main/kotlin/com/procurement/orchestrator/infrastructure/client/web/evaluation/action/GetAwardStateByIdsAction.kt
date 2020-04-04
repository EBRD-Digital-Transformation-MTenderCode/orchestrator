package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.award.AwardStatus
import com.procurement.orchestrator.domain.model.award.AwardStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import java.io.Serializable

abstract class GetAwardStateByIdsAction :
    FunctionalAction<GetAwardStateByIdsAction.Params, GetAwardStateByIdsAction.Result> {
    override val version: String = "2.0.0"
    override val name: String = "getAwardStateByIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("awardIds") @param:JsonProperty("awardIds") val ids: List<AwardId>
    )

    class Result(values: List<Award>) : List<Result.Award> by values, Serializable {
        class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails
        ) : Serializable
    }
}
