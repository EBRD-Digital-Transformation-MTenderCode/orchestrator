package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.qualification.QualificationStatus
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class AnalyzeQualificationForInvitationAction : FunctionalAction<AnalyzeQualificationForInvitationAction.Params, AnalyzeQualificationForInvitationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "analyzeQualificationsForInvitation"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethodDetails,
        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
    )

    class Result(
        @field:JsonProperty("qualifications") @param:JsonProperty("qualifications") val qualifications: List<Qualification>
    ) : Serializable {
        class Qualification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: QualificationId,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: QualificationStatus,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: QualificationStatusDetails,
            @field:JsonProperty("relatedSubmission") @param:JsonProperty("relatedSubmission") val relatedSubmission: SubmissionId
        ) : Serializable
    }
}
