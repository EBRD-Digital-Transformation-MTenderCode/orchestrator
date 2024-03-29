package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.process.AdditionalProcess
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.document.ProcessInitiator
import com.procurement.orchestrator.domain.model.tender.AwardCriteria
import java.io.Serializable

data class ProcessInfo(

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid?,

    @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
    @field:JsonProperty("stage") @param:JsonProperty("stage") val stage: Stage,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("prevStage") @param:JsonProperty("prevStage") val prevStage: Stage?,

    @field:JsonProperty("processType") @param:JsonProperty("processType") val processDefinitionKey: ProcessDefinitionKey,
    @field:JsonProperty("phase") @param:JsonProperty("phase") val phase: Phase,
    @get:JsonProperty("isAuction") @param:JsonProperty("isAuction") val isAuction: Boolean,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") val mainProcurementCategory: String?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedProcess") @param:JsonProperty("relatedProcess") val relatedProcess: RelatedProcess?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("additionalProcess") @param:JsonProperty("additionalProcess") val additionalProcess: AdditionalProcess?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("entityType") @param:JsonProperty("entityType") val entityType: String?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("entityId") @param:JsonProperty("entityId") val entityId: String?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("processInitiator") @param:JsonProperty("processInitiator") val processInitiator: ProcessInitiator?

) : Serializable {

    data class RelatedProcess(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid?
    ) : Serializable

}
