package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatus
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FindQualificationIdsAction : FunctionalAction<FindQualificationIdsAction.Params, FindQualificationIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "findQualificationIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("states") @param:JsonProperty("states") val states: List<State>
    ) {
        data class State(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("status") @param:JsonProperty("status") val status: QualificationStatus? = null,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails? = null
        )
    }

    class Result(qualificationIds: List<QualificationId>) : List<QualificationId> by qualificationIds, Serializable
}
