package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.AmendmentRelatesTo
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus
import com.procurement.orchestrator.domain.model.amendment.AmendmentType
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateAmendmentAction : FunctionalAction<CreateAmendmentAction.Params, CreateAmendmentAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createAmendment"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
        @field:JsonProperty("relatedEntityId") @param:JsonProperty("relatedEntityId") val relatedEntityId: String,
        @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment
    ) {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId.Permanent,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

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

    class Result(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId.Permanent,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

        @field:JsonProperty("type") @param:JsonProperty("type") val type: AmendmentType,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus,
        @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo,
        @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
    ) : Serializable {

        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
        ) : Serializable
    }
}
