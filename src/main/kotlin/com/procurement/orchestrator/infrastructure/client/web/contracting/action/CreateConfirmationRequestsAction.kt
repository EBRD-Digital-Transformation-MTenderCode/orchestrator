package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequest
import com.procurement.orchestrator.domain.model.contract.confirmation.request.RequestGroup
import com.procurement.orchestrator.domain.model.contract.confirmation.request.RequestGroups
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class CreateConfirmationRequestsAction : FunctionalAction<CreateConfirmationRequestsAction.Params, CreateConfirmationRequestsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createConfirmationRequests"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("role") @field:JsonProperty("role") val role: String,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("access") @field:JsonProperty("access") val access: Access?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("dossier") @field:JsonProperty("dossier") val dossier: Dossier?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("submission") @field:JsonProperty("submission") val submission: Submission?
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
        ) {
            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId
            )
        }

        data class Access(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("buyers") @field:JsonProperty("buyers") val buyers: List<Buyer>?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("procuringEntity") @field:JsonProperty("procuringEntity") val procuringEntity: ProcuringEntity?
        ) {
            data class Buyer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String
            )

            data class ProcuringEntity(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner
            )
        }

        data class Dossier(
            @param:JsonProperty("candidates") @field:JsonProperty("candidates") val candidates: List<Candidate>
        ) {
            data class Candidate(
                @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
                @param:JsonProperty("organizations") @field:JsonProperty("organizations") val organizations: List<Organization>
            ) {
                data class Organization(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                )
            }
        }

        data class Submission(
            @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>
        ) {
            data class Tenderer(
                @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
                @param:JsonProperty("organizations") @field:JsonProperty("organizations") val organizations: List<Organization>
            ) {
                data class Organization(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                )
            }
        }
    }

    data class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) : Serializable {

        data class Contract(
            @param:JsonProperty("confirmationRequests") @field:JsonProperty("confirmationRequests") val confirmationRequests: List<ConfirmationRequest>
        ) : Serializable {

            data class ConfirmationRequest(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("type") @field:JsonProperty("type") val type: String,
                @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: String,
                @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String,
                @param:JsonProperty("source") @field:JsonProperty("source") val source: String,
                @param:JsonProperty("requests") @field:JsonProperty("requests") val requests: List<Request>
            ) : Serializable {

                data class Request(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("relatedOrganization") @field:JsonProperty("relatedOrganization") val relatedOrganization: RelatedOrganization,
                    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
                    @param:JsonProperty("token") @field:JsonProperty("token") val token: Token
                ) : Serializable {

                    data class RelatedOrganization(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    ) : Serializable
                }
            }
        }
    }

    object ResponseConverter {

        object Contract {
            fun Result.Contract.ConfirmationRequest.toDomain(): ConfirmationRequest =
                ConfirmationRequest(
                    id = id,
                    type = type,
                    relatesTo = relatesTo,
                    relatedItem = relatedItem,
                    source = source,
                    requests = requests.map { requestGroup ->
                        RequestGroup(
                            id = requestGroup.id,
                            relatedOrganization = requestGroup.relatedOrganization.let { relatedOrganization ->
                                OrganizationReference(
                                    id = relatedOrganization.id,
                                    name = relatedOrganization.name
                                )
                            },
                            owner = requestGroup.owner,
                            token = requestGroup.token
                        )
                    }.let { RequestGroups(it) }
                )
        }
    }
}
