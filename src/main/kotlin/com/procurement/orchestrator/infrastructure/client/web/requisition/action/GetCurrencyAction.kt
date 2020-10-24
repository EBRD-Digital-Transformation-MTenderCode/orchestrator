package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetCurrencyAction : FunctionalAction<GetCurrencyAction.Params, GetCurrencyAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getCurrency"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        class Tender(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
        ) : Serializable {
            class Value(@field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String) : Serializable
        }
    }
}
