package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.measure.Amount
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class CalculateApValueAction : FunctionalAction<CalculateApValueAction.Params, CalculateApValueAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "calculateAPValue"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) : Serializable {

        data class Tender(
            @param:JsonProperty("value") @field:JsonProperty("value") val value: Value
        ) : Serializable {
            data class Value(
                @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: Amount,
                @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
            ) : Serializable
        }
    }
}
