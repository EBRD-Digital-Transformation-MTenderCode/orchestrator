package com.procurement.orchestrator.domain.model.organization.datail.permit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Permit(
    //TODO id & scheme ?
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: PermitScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: PermitId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("url") @param:JsonProperty("url") val url: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("permitDetails") @param:JsonProperty("permitDetails") val permitDetails: PermitDetails? = null
) : IdentifiableObject<Permit>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Permit
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun updateBy(src: Permit) = Permit(
        scheme = scheme,
        id = id,
        url = src.url or url,
        permitDetails = permitDetails updateBy src.permitDetails
    )
}
