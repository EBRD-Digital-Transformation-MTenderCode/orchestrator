package com.procurement.orchestrator.domain.model.person

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunctions
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Person(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: PersonId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: BusinessFunctions = BusinessFunctions()
) : IdentifiableObject<Person>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Person
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Person) = Person(
        id = id,
        identifier = identifier updateBy src.identifier,
        name = src.name or name,
        title = src.title or title,
        businessFunctions = businessFunctions updateBy src.businessFunctions
    )
}
