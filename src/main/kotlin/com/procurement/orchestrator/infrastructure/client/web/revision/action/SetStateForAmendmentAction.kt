package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.AmendmentStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class SetStateForAmendmentAction :
    FunctionalAction<SetStateForAmendmentAction.Params, SetStateForAmendmentAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setStateForAmendment"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment
    ) {

        class Amendment(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId.Permanent,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus
        ) : Serializable
    }

    class Result(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId.Permanent,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus
    ) : Serializable
}
