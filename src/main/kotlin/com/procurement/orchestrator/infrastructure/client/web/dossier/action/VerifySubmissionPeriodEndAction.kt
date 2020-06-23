package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class VerifySubmissionPeriodEndAction : FunctionalAction<VerifySubmissionPeriodEndAction.Params, VerifySubmissionPeriodEndAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "verifySubmissionPeriodEnd"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime
    )

    class Result(
        @get:JsonProperty("submissionPeriodExpired") @param:JsonProperty("submissionPeriodExpired") val submissionPeriodExpired : Boolean
    ): Serializable
}
