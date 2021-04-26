package com.procurement.orchestrator.domain.model.process.master.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class ProcuringEntity(
    @get:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @get:JsonProperty("name") @param:JsonProperty("name") val name: String,
    @get:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner
) : IdentifiableObject<ProcuringEntity>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ProcuringEntity
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: ProcuringEntity) = ProcuringEntity(
        id = id,
        name = src.name or name,
        owner = src.owner or owner
    )
}
