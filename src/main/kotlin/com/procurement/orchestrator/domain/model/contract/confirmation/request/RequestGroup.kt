package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.updateBy

import java.io.Serializable

data class RequestGroup(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequestGroupId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requests") @param:JsonProperty("requests") val requests: Requests = Requests(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedOrganization") @param:JsonProperty("relatedOrganization") val relatedOrganization: OrganizationReference? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner

    ) : IdentifiableObject<RequestGroup>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RequestGroup
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: RequestGroup) = RequestGroup(
        id = id,
        requests = requests updateBy src.requests,
        token = src.token or token,
        owner = src.owner or owner,
        relatedOrganization = relatedOrganization updateBy src.relatedOrganization
    )
}
