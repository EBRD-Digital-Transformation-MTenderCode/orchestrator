package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CloseAwardPeriodAction :
    FunctionalAction<CloseAwardPeriodAction.Params, CloseAwardPeriodAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "closeAwardPeriod"
    override val target: Target<Result> = Target.single()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
    )

    class Result(
        @field:JsonProperty("awardPeriod") @param:JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
    ) : Serializable {

        class AwardPeriod(
            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
        ) : Serializable
    }
}
