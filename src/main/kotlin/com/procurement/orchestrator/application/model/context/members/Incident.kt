package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class Incident(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: String,
    @field:JsonProperty("level") @param:JsonProperty("level") val level: String,
    @field:JsonProperty("service") @param:JsonProperty("service") val service: Service,
    @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
) : Serializable {

    data class Service(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
        @field:JsonProperty("version") @param:JsonProperty("version") val version: String
    ) : Serializable

    data class Detail(
        @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("metadata") @param:JsonProperty("metadata") val metadata: String = ""
    ) : Serializable
}
