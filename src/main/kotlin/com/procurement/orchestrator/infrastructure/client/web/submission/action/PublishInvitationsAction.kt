package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.invitation.Invitation
import com.procurement.orchestrator.domain.model.invitation.InvitationId
import com.procurement.orchestrator.domain.model.invitation.InvitationStatus
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.OrganizationReferences
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class PublishInvitationsAction : FunctionalAction<PublishInvitationsAction.Params, PublishInvitationsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "publishInvitations"
    override val target: Target<Result> = Target.single()

    class Params(
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid
    )

    class Result(
        @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
    ) : Serializable {

        data class Invitation(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("relatedQualification") @field:JsonProperty("relatedQualification") val relatedQualification: QualificationId,
            @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderers>
        ) : Serializable {

            data class Tenderers(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            ) : Serializable
        }
    }
}

fun PublishInvitationsAction.Result.Invitation.convertIdAndStatus() =
    Invitation(
        id = this.id,
        status = this.status
    )

fun PublishInvitationsAction.Result.Invitation.convertFull() =
    Invitation(
        id = id,
        date = date,
        status = status,
        relatedQualification = relatedQualification,
        tenderers = tenderers.map { tenderer ->
            OrganizationReference(
                id = tenderer.id,
                name = tenderer.name
            )
        }.let { OrganizationReferences(it) }
    )