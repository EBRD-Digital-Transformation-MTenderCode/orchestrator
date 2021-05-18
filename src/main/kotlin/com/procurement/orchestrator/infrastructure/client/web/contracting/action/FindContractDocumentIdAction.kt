package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.document.ProcessInitiator
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindContractDocumentIdAction :
    FunctionalAction<FindContractDocumentIdAction.Params, FindContractDocumentIdAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findContractDocumentId"
    override val target: Target<Result> = Target.single()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("processInitiator") @param:JsonProperty("processInitiator") val processInitiator: ProcessInitiator?,

        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId
        )
    }

    data class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) : Serializable {

        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
        ) : Serializable {

            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            ) : Serializable

        }
    }
}

fun FindContractDocumentIdAction.Result.Contract.toDomain() =
    Contract(
        id = ContractId.create(id),
        documents = documents.orEmpty()
            .map { it.toDomain() }
            .let { Documents(it) }
    )

fun FindContractDocumentIdAction.Result.Contract.Document.toDomain() =
    Document(id = DocumentId.create(id))

