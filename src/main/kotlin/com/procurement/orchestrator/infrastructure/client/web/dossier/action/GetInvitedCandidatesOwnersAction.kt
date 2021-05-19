package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.process.master.data.Candidates
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class GetInvitedCandidatesOwnersAction :
    FunctionalAction<GetInvitedCandidatesOwnersAction.Params, GetInvitedCandidatesOwnersAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "getInvitedCandidatesOwners"
    override val target: Target<Result> = Target.single()

    data class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid
    )

    class Result(
        @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: List<Candidate>
    ) : Serializable {
        data class Candidate(
            @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
            @field:JsonProperty("organizations") @param:JsonProperty("organizations") val organizations: List<Organization>
        ) : Serializable{
            data class Organization(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            ) : Serializable
        }
    }
}