package com.procurement.orchestrator.infrastructure.client.web.qualification.action

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

abstract class FinalizeQualificationsAction : FunctionalAction<FinalizeQualificationsAction.Params, FinalizeQualificationsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "finalizeQualifications"
    override val target: Target<Result> = Target.plural()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @param:JsonProperty("qualifications") @field:JsonProperty("qualifications") val qualifications: List<Qualification>
    ) : Serializable {

        data class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: QualificationStatus,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails
        ) : Serializable
    }
}
