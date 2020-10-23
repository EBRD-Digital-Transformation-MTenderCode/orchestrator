package com.procurement.orchestrator.domain.model.unit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class Unit(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UnitId?,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : IdentifiableObject<Unit>, Serializable {

    override fun equals(other: Any?): Boolean =
        if (this === other)
            true
        else
            other is Unit && this.id == other.id

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun updateBy(src: Unit) = Unit(
        id = id,
        name = src.name or name,
        value = src.value or value,
        uri = src.uri or uri
    )
}
