package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModality
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckExistenceSignAuctionAction : ProceduralAction<CheckExistenceSignAuctionAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkExistenceSignAuction"

    class Params(
        @field:JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid?,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") var ocid: Ocid?,

        @field:JsonProperty("tender") @param:JsonProperty("tender") var tender: Tender
    ) {
        data class Tender(
            @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>
        )
    }
}
