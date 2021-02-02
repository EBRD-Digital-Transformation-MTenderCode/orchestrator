package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.ContractId
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.OrganizationReferences
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class AddSupplierReferencesInFCAction : FunctionalAction<AddSupplierReferencesInFCAction.Params, AddSupplierReferencesInFCAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "addSupplierReferencesInFC"
    override val target: Target<Result> = Target.single()

    data class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
    ) {
        data class Party(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
        )
    }

    data class Result(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: ContractId,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>,
        @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean
    ) : Serializable {

        data class Supplier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        ) : Serializable

        fun toDomain(): Contract =
            Contract(
                id = id,
                status = status,
                statusDetails = statusDetails,
                date = date,
                isFrameworkOrDynamic = isFrameworkOrDynamic,
                suppliers = suppliers
                    .map { it.convert() }
                    .let { OrganizationReferences(it) }
            )

        private fun Supplier.convert(): OrganizationReference =
            OrganizationReference(
                id = id,
                name = name
            )
    }
}


