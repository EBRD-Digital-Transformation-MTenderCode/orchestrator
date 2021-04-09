package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.bid.BidId
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckExistenceOfInvitationAction : ProceduralAction<CheckExistenceOfInvitationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkExistenceOfInvitation"

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
    ) {
        data class Bids(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
        ) {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,
                @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>
            ) {
                data class Tenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId
                )
            }
        }
    }
}
