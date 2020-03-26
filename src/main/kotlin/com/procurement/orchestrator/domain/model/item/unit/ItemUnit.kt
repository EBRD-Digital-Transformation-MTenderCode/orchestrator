package com.procurement.orchestrator.domain.model.item.unit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.value.Value
import java.io.Serializable

data class ItemUnit(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: ItemUnitScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ItemUnitId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ItemUnit
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
