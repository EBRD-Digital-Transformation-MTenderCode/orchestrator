package com.procurement.orchestrator.domain.model.organization.datail

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class MainEconomicActivity(
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: MainEconomicActivityScheme?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("id") @param:JsonProperty("id") val id: MainEconomicActivityId?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
)  : IdentifiableObject<MainEconomicActivity>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is MainEconomicActivity
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun updateBy(src: MainEconomicActivity) = MainEconomicActivity(
        scheme = src.scheme,
        id = src.id,
        description = src.description or description,
        uri = src.uri or uri
    )
}
