package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.infrastructure.model.Version

abstract class DataValidationAction : ProceduralAction<DataValidationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "dataValidation"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment
    ) {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId.Temporal,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
        ) {

            data class Document(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
            )
        }
    }
}
