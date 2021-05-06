package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.contract.confirmation.response.ConfirmationResponseId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckAccessToRequestOfConfirmationAction :
    ProceduralAction<CheckAccessToRequestOfConfirmationAction.Params> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkAccessToRequestOfConfirmation"

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId,
            @field:JsonProperty("confirmationResponses") @param:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>
        ) {
            data class ConfirmationResponse(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: ConfirmationResponseId,
                @field:JsonProperty("requestId") @param:JsonProperty("requestId") val requestId: String?
            )
        }
    }
}
