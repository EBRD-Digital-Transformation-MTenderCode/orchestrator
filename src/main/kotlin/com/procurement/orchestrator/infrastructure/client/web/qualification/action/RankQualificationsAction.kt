package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.tender.criteria.CriteriaSource
import com.procurement.orchestrator.domain.model.tender.criteria.QualificationSystemMethod
import com.procurement.orchestrator.domain.model.tender.criteria.ReductionCriteria
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class RankQualificationsAction : FunctionalAction<RankQualificationsAction.Params, RankQualificationsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "rankQualifications"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: List<Submission>,
        @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
    ) {
        data class Submission(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId?,
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime?
        )

        data class Tender(
            @field:JsonProperty("otherCriteria") @param:JsonProperty("otherCriteria") val otherCriteria: OtherCriteria,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criteria>?
        ) {
            data class OtherCriteria(
                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("qualificationSystemMethods") @param:JsonProperty("qualificationSystemMethods") val qualificationSystemMethods: List<QualificationSystemMethod>?,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("reductionCriteria") @param:JsonProperty("reductionCriteria") val reductionCriteria: ReductionCriteria?
            )

            data class Criteria(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @JsonInclude(JsonInclude.Include.NON_NULL)

                @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource?
            )
        }
    }

    class Result(qualifications: List<Qualification>) : List<Result.Qualification> by qualifications, Serializable {

        class Qualification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: QualificationId,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails
        ) : Serializable
    }
}
