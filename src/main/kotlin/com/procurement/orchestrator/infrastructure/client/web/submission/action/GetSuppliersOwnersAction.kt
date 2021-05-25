package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.process.master.data.Organization
import com.procurement.orchestrator.domain.model.process.master.data.Organizations
import com.procurement.orchestrator.domain.model.process.master.data.Tenderer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetSuppliersOwnersAction :
    FunctionalAction<GetSuppliersOwnersAction.Params, GetSuppliersOwnersAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getSuppliersOwners"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
    ) {
        class Contract(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
        ) {
            class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )
        }
    }

    class Result(
        @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>
    ) : Serializable {

        data class Tenderer(
            @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
            @param:JsonProperty("organizations") @field:JsonProperty("organizations") val organizations: List<Organization>
        ) : Serializable

        data class Organization(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        ) : Serializable
    }
}

fun GetSuppliersOwnersAction.Result.Tenderer.toDomain(): Tenderer {
    return Tenderer(
        owner = owner,
        organizations = organizations
            .map { Organization(id = it.id, name = it.name) }
            .let { Organizations(it) }
    )
}

