package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckExistenceSupplierReferencesInFCAction : ProceduralAction<CheckExistenceSupplierReferencesInFCAction.Params> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkExistenceSupplierReferencesInFC"

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        data class Contract(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractId
        )
    }
}