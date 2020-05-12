package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.SubmissionStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class SetStateForSubmissionAction : FunctionalAction<SetStateForSubmissionAction.Params, SetStateForSubmissionAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "setStateForSubmission"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("submission") @param:JsonProperty("submission") val submission: Submission
    ) {
        class Submission(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: SubmissionStatus
        )
    }

    class Result(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: SubmissionStatus
    ) : Serializable
}
