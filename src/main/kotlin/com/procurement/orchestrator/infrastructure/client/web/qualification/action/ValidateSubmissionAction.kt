package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class ValidateSubmissionAction : ProceduralAction<ValidateSubmissionAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateSubmission"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: SubmissionId.Permanent,
        @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: Candidates,
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents
    )
}
