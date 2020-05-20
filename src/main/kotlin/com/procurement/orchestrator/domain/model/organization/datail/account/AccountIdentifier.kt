package com.procurement.orchestrator.domain.model.organization.datail.account

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import java.io.Serializable

data class AccountIdentifier(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: AccountIdentifierScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AccountIdentifierId
) : IdentifiableObject<AccountIdentifier>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is AccountIdentifier
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun updateBy(src: AccountIdentifier) = AccountIdentifier(
        scheme = src.scheme,
        id = src.id
    )
}
