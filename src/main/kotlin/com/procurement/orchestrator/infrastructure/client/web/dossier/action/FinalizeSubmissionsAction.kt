package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatus
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.SubmissionStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class FinalizeSubmissionsAction : FunctionalAction<FinalizeSubmissionsAction.Params, FinalizeSubmissionsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "finalizeSubmissions"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("qualifications") @param:JsonProperty("qualifications") val qualifications: List<Qualification>
    ) {
        data class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("status") @field:JsonProperty("status") val status: QualificationStatus?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatedSubmission") @field:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId?
        )
    }

    class Result(
        @field:JsonProperty("submissions") @param:JsonProperty("submissions") val submissions: Submission
    ) : Serializable {
        data class Submission(
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Details>
        ) : Serializable {
            data class Details(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: SubmissionId,
                @param:JsonProperty("status") @field:JsonProperty("status") val status: SubmissionStatus
            ) : Serializable
        }
    }
}

fun FinalizeSubmissionsAction.Result.Submission.Details.convertToGlobalContextEntity(): Submission =
    Submission(id = this.id, status = this.status)