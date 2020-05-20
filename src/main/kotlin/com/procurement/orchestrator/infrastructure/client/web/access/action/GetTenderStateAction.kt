package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.tender.TenderStatus
import com.procurement.orchestrator.domain.model.tender.TenderStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetTenderStateAction : FunctionalAction<GetTenderStateAction.Params, GetTenderStateAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getTenderState"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: TenderStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: TenderStatusDetails
    ) : Serializable
}
