package com.procurement.orchestrator.domain.model.requirement.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction

data class Responder(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
)
