package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.tender.conversion.Conversions
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethods
import com.procurement.orchestrator.domain.model.tender.criteria.ReductionCriteria
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetQualificationCriteriaAndMethodAction : FunctionalAction<GetQualificationCriteriaAndMethodAction.Params, GetQualificationCriteriaAndMethodAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getQualificationCriteriaAndMethod"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: Conversions = Conversions(),

        @field:JsonProperty("qualificationSystemMethods") @param:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: QualificationSystemMethods,
        @field:JsonProperty("reductionCriteria") @param:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria
    ) : Serializable
}
