package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class StartQualificationPeriodAction : FunctionalAction<StartQualificationPeriodAction.Params,StartQualificationPeriodAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "startQualificationPeriod"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime
    )

    class Result(
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
    ) : Serializable
}
