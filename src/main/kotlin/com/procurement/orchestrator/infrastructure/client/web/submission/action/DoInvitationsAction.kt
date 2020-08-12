package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.invitation.InvitationId
import com.procurement.orchestrator.domain.model.invitation.InvitationStatus
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class DoInvitationsAction : FunctionalAction<DoInvitationsAction.Params, DoInvitationsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "doInvitations"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("qualifications") @field:JsonProperty("qualifications") val qualifications: List<Qualification>?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("submissions") @field:JsonProperty("submissions") val submissions: Submissions?
    ) {
        data class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatedSubmission") @field:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId?
        )

        data class Submissions(
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>?
        ) {
            data class Detail(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: SubmissionId,
                @param:JsonProperty("candidates") @field:JsonProperty("candidates") val candidates: List<Candidate>
            ) {
                data class Candidate(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                )
            }
        }
    }

    class Result(
        @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
    ) : Serializable {
        data class Invitation(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus,
            @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,
            @param:JsonProperty("relatedQualification") @field:JsonProperty("relatedQualification") val relatedQualification: QualificationId
        ) : Serializable {
            data class Tenderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            ) : Serializable
        }
    }
}
