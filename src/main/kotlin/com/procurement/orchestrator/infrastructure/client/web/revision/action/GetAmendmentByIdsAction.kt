package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.AmendmentRelatesTo
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus
import com.procurement.orchestrator.domain.model.amendment.AmendmentType
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetAmendmentByIdsAction :
    FunctionalAction<GetAmendmentByIdsAction.Params, GetAmendmentByIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getAmendmentByIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("amendmentIds") @param:JsonProperty("amendmentIds") val ids: List<AmendmentId.Permanent>
    )

    class Result(values: List<Amendment>) : List<Result.Amendment> by values, Serializable {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId.Permanent,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: AmendmentType,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String
        ) : Serializable
    }
}
