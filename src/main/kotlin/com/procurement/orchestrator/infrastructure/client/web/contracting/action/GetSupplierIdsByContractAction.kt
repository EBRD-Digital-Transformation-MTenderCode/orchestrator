package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId

abstract class GetSupplierIdsByContractAction :
    FunctionalAction<GetSupplierIdsByContractAction.Params, GetSupplierIdsByContractAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getSupplierIdsByContract"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId
        )
    }

    data class Result(
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
        ) {
            data class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )
        }
    }
}