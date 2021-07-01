package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.Country
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckRelatedContractsStateAction : ProceduralAction<CheckRelatedContractsStateAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkContractState"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
        @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId
        )
    }
}