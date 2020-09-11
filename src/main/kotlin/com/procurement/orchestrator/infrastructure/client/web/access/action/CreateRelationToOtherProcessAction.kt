package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.RelatedProcessScheme
import com.procurement.orchestrator.domain.model.contract.RelatedProcessType
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class CreateRelationToOtherProcessAction : FunctionalAction<CreateRelationToOtherProcessAction.Params, CreateRelationToOtherProcessAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createRelationToOtherProcess"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedCpid") @param:JsonProperty("relatedCpid") val relatedCpid: Cpid?,

        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
        )

    class Result(
        @param:JsonProperty("relatedProcesses") @field:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcess>
    ) : Serializable {
        data class RelatedProcess(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("relationship") @field:JsonProperty("relationship") val relationship: List<RelatedProcessType>,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: RelatedProcessScheme,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: String,
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
        ) : Serializable
    }
}
