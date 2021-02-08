package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetOcidFromRelatedProcessAction : FunctionalAction<GetOcidFromRelatedProcessAction.Params, GetOcidFromRelatedProcessAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getOcidFromRelatedProcess"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
    )

    class Result(
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    ) : Serializable
}

