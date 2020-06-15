package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethod
import com.procurement.orchestrator.domain.model.tender.criteria.ReductionCriteria
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class DetermineNextsForQualificationAction : FunctionalAction<DetermineNextsForQualificationAction.Params, DetermineNextsForQualificationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "determineNextsForQualification"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: List<Submission>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("reductionCriteria") @param:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria?,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("qualificationSystemMethods") @param:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: List<QualificationSystemMethod>?
    ) {

        data class Submission(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId.Permanent?,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime?
        )
    }

    class Result(qualifications: List<Qualification>) : List<Result.Qualification> by qualifications, Serializable {

        class Qualification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: QualificationId,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails
        ) : Serializable
    }
}
