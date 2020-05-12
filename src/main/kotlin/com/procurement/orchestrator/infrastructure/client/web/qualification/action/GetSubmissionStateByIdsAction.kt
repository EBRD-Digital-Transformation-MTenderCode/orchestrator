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

abstract class GetSubmissionStateByIdsAction : FunctionalAction<GetSubmissionStateByIdsAction.Params, GetSubmissionStateByIdsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getSubmissionStateByIds"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("submissionIds") @param:JsonProperty("submissionIds") val submissionIds: List<SubmissionId>
    )

    class Result(submissions: List<Submission>) : List<Result.Submission> by submissions, Serializable {

        class Submission(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: SubmissionStatus
        ) : Serializable
    }
}
