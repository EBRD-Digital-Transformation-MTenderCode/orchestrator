package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetOrganizationIdAndSourceOfRequestGroupAction : FunctionalAction<GetOrganizationIdAndSourceOfRequestGroupAction.Params, GetOrganizationIdAndSourceOfRequestGroupAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getRequestByConfirmationResponse"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("confirmationResponses") @field:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>
        ) {
            data class ConfirmationResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ConfirmationResponseId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: String?
            )
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
                @param:JsonProperty("requests") @field:JsonProperty("requests") val requests: List<RequestGroup>
            ) : Serializable {
                data class RequestGroup(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("relatedOrganization") @field:JsonProperty("relatedOrganization") val relatedOrganization: RelatedOrganization,
                    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
                    @param:JsonProperty("token") @field:JsonProperty("token") val token: String
                ) : Serializable {
                    data class RelatedOrganization(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    ) : Serializable
                }
            }
        }
    }
}
