package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckEqualityCurrenciesAction : ProceduralAction<CheckEqualityCurrenciesAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkEqualityCurrencies"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("cpidAP") @param:JsonProperty("cpidAP") val cpidAP: Cpid?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("ocidAP") @param:JsonProperty("ocidAP") val ocidAP: Ocid?
    )
}
