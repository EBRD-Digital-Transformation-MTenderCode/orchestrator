package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.invitation.InvitationId
import com.procurement.orchestrator.domain.model.invitation.InvitationStatus
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class PublishInvitationsAction : FunctionalAction<PublishInvitationsAction.Params, PublishInvitationsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "publishInvitations"
    override val target: Target<Result> = Target.plural()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid
    )

    class Result(
        @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
    ) : Serializable {

        data class Invitation(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus
        ) : Serializable
    }
}
