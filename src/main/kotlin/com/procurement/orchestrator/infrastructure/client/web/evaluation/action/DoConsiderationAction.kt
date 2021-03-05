package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.award.AwardStatus
import com.procurement.orchestrator.domain.model.award.AwardStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class DoConsiderationAction :
    FunctionalAction<DoConsiderationAction.Params, DoConsiderationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "doConsideration"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
    ) {
        class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId
        )
    }

    class Result(
        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
    ) : Serializable {
        data class Award(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardStatus,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
            @param:JsonProperty("relatedBid") @field:JsonProperty("relatedBid") val relatedBid: String
        ) : Serializable
    }
}

fun DoConsiderationAction.Result.Award.toDomain(): Award {
    return Award(
        id = id,
        status = status,
        statusDetails = statusDetails,
        relatedBid = relatedBid
    )
}
