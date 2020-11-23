package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime
import com.procurement.orchestrator.domain.model.contract.Contract as DomainContract

abstract class CreateFrameworkContractAction : FunctionalAction<CreateFrameworkContractAction.Params, CreateFrameworkContractAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createFrameworkContract"
    override val target: Target<Result> = Target.single()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner
    )

    data class Result(
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contact>,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token
    ) : Serializable {

        data class Contact(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
            @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean
        ) : Serializable
    }

    object ResponseConverter {

        object Contract {
            fun toDomain(contract: Result.Contact): DomainContract =
                DomainContract(
                    id = contract.id,
                    status = contract.status,
                    statusDetails = contract.statusDetails,
                    date = contract.date,
                    isFrameworkOrDynamic = contract.isFrameworkOrDynamic
                )
        }
    }
}
