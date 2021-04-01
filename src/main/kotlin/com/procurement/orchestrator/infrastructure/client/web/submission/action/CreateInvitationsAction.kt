package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.invitation.InvitationId
import com.procurement.orchestrator.domain.model.invitation.InvitationStatus
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class CreateInvitationsAction : FunctionalAction<CreateInvitationsAction.Params, CreateInvitationsAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createInvitations"
    override val target: Target<Result> = Target.single()

    class Params(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: Cpid?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("additionalCpid") @field:JsonProperty("additionalCpid") val additionalCpid: Cpid?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("additionalOcid") @field:JsonProperty("additionalOcid") val additionalOcid: Ocid?,

        @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
    ) {
        data class Tender(
            @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
        ) {
            data class Lot(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
            )
        }
    }

    class Result(
        @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
    ) : Serializable {

        data class Invitation(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus,
            @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>
        ) : Serializable {

            data class Tenderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            ) : Serializable
        }
    }
}