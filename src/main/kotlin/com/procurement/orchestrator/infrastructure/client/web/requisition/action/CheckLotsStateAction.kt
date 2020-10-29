package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class CheckLotsStateAction : ProceduralAction<CheckLotsStateAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkLotsState"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        class Tender(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>?
        ) : Serializable {
            class Lot(@field:JsonProperty("id") @param:JsonProperty("id") val id: LotId) : Serializable
        }
    }
}
