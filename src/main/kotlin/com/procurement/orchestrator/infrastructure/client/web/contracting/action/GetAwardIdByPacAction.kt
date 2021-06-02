package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetAwardIdByPacAction : FunctionalAction<GetAwardIdByPacAction.Params, GetAwardIdByPacAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getPac"
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
            @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId
        ) : Serializable
    }
}

fun GetAwardIdByPacAction.Result.Contact.toDomain() = Contract(id = id, awardId = awardId)
