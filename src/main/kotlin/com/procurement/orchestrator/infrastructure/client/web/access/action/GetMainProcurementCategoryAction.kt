package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.tender.ProcurementCategory
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetMainProcurementCategoryAction : FunctionalAction<GetMainProcurementCategoryAction.Params, GetMainProcurementCategoryAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getMainProcurementCategory"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) : Serializable {
        data class Tender(
            @field:JsonProperty("mainProcurementCategory") @param:JsonProperty("mainProcurementCategory") val mainProcurementCategory: ProcurementCategory
        ) : Serializable
    }
}
