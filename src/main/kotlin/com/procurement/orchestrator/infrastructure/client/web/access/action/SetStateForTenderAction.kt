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

abstract class SetStateForTenderAction :
    FunctionalAction<SetStateForTenderAction.Params, SetStateForTenderAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setStateForTender"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @param:JsonProperty("status") @field:JsonProperty("status") val status: TenderStatus,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: TenderStatusDetails
        )
    }

    class Result(
        @param:JsonProperty("status") @field:JsonProperty("status") val status: TenderStatus,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: TenderStatusDetails
    ) : Serializable
}
