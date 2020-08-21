package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckTenderStateAction : ProceduralAction<CheckTenderStateAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkTenderState"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
    )

}
