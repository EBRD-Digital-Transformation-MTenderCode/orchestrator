package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import com.procurement.orchestrator.domain.model.contract.Contract as DomainContract

abstract class SetStateForContractsAction : FunctionalAction<SetStateForContractsAction.Params, SetStateForContractsAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setStateForContracts"
    override val target: Target<Result> = Target.single()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ){
        data class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
            )
        }
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
            fun toDomain(contract: Result.Contact): DomainContract =
                DomainContract(
                    id = contract.id,
                    status = contract.status,
                    statusDetails = contract.statusDetails
                )
        }
    }
}
