package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.RelatedProcess
import com.procurement.orchestrator.domain.model.contract.RelatedProcessId
import com.procurement.orchestrator.domain.model.contract.RelatedProcessScheme
import com.procurement.orchestrator.domain.model.contract.RelatedProcessType
import com.procurement.orchestrator.domain.model.contract.RelatedProcessTypes
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class CreateRelationToContractProcessStageAction : FunctionalAction<CreateRelationToContractProcessStageAction.Params, CreateRelationToContractProcessStageAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createRelationToContractProcessStage"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedOcid") @param:JsonProperty("relatedOcid") val relatedOcid: Ocid?,

        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess

    )

    class Result(
        @param:JsonProperty("relatedProcesses") @field:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcess>
    ) : Serializable {
        data class RelatedProcess(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: RelatedProcessId,
            @param:JsonProperty("relationship") @field:JsonProperty("relationship") val relationship: List<RelatedProcessType>,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: RelatedProcessScheme,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: String,
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
        ) : Serializable
    }
}

fun CreateRelationToContractProcessStageAction.Result.RelatedProcess.convert() = RelatedProcess(
    id = id,
    scheme = scheme,
    identifier = identifier,
    relationship = RelatedProcessTypes(relationship),
    uri = uri
)
