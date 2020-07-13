package com.procurement.orchestrator.domain.model.invitation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.OrganizationReferences
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import java.io.Serializable
import java.time.LocalDateTime

data class Invitation(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: InvitationId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: InvitationStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("tenderers") @param:JsonProperty("tenderers") val tenderers: OrganizationReferences = OrganizationReferences(),

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedQualification") @param:JsonProperty("relatedQualification") val relatedQualification: QualificationId? = null

    ) : IdentifiableObject<Invitation>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Invitation
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Invitation): Invitation = Invitation(
        id = id,
        date = src.date or date,
        status = src.status or status,
        tenderers = tenderers updateBy src.tenderers,
        relatedQualification = src.relatedQualification or relatedQualification
    )
}
