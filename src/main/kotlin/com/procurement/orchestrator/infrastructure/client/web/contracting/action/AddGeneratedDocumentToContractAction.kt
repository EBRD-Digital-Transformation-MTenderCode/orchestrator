package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.DocumentInitiator
import com.procurement.orchestrator.domain.model.document.DocumentType
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import com.procurement.orchestrator.domain.model.contract.Contract as DomainContract
import com.procurement.orchestrator.domain.model.document.Document as DomainDocument

abstract class AddGeneratedDocumentToContractAction : FunctionalAction<AddGeneratedDocumentToContractAction.Params, AddGeneratedDocumentToContractAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "addGeneratedDocumentToContract"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("documentInitiator") @field:JsonProperty("documentInitiator") val documentInitiator: DocumentInitiator?,

        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
        ) {
            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId
            )
        }
    }

    data class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) : Serializable {

        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
        ): Serializable {

            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String
            ) : Serializable

            fun toDomain(): DomainContract =
                DomainContract(
                    id = id,
                    status = status,
                    statusDetails = statusDetails,
                    documents = documents
                        .map { it.convert() }
                        .let { Documents(it) }
                )

            private fun Document.convert(): DomainDocument =
                DomainDocument(
                    id = DocumentId.create(id),
                    title = "",
                    documentType = DocumentType.creator(documentType)
                )
        }


    }
}


