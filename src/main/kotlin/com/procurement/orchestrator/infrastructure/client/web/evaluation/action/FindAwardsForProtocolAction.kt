package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.organization.Organizations
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindAwardsForProtocolAction :
    FunctionalAction<FindAwardsForProtocolAction.Params, FindAwardsForProtocolAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findAwardsForProtocol"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {
        class Tender(
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
        ) {
            class Lot(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
            )
        }
    }

    class Result(
        @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
    ) : Serializable {
        data class Award(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,
            @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
        ) : Serializable {
            data class Supplier(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }
}

fun FindAwardsForProtocolAction.Result.Award.toDomain(): Award {
    return Award(
        id = id,
        suppliers = suppliers.map { supplier ->
            Organization(
                id = supplier.id,
                name = supplier.name
            )
        }.let { Organizations(it) }
    )
}
