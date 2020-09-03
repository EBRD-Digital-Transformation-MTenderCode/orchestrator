package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckRelationAction : ProceduralAction<CheckRelationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkRelation"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedCpid") @param:JsonProperty("relatedCpid") val relatedCpid: Cpid?,

        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("existenceRelation") @param:JsonProperty("existenceRelation") val existenceRelation: Boolean?
    )
}
