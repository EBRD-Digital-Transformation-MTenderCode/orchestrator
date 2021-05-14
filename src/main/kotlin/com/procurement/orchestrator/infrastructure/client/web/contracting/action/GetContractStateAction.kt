package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetContractStateAction : FunctionalAction<GetContractStateAction.Params, GetContractStateAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getContractState"
    override val target: Target<Result> = Target.plural()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId
        )
    }

    data class Result(
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contact>
    ) : Serializable {

        data class Contact(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
        ) : Serializable
    }

    object ResponseConverter {

        object Contract {
            fun toDomain(contract: Result.Contact): com.procurement.orchestrator.domain.model.contract.Contract =
                Contract(
                    id = contract.id,
                    status = contract.status,
                    statusDetails = contract.statusDetails
                )
        }
    }
}
