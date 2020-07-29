package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class SetQualificationPeriodEndAction : FunctionalAction<SetQualificationPeriodEndAction.Params, SetQualificationPeriodEndAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setQualificationPeriodEnd"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
    )

    class Result(
        @param:JsonProperty("preQualification") @field:JsonProperty("preQualification") val preQualification: PreQualification
    ) : Serializable {
        data class PreQualification(
            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period
        ) : Serializable {
            data class Period(
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
            ) : Serializable
        }
    }
}
