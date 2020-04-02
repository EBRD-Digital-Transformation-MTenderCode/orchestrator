package com.procurement.orchestrator.infrastructure.client.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

sealed class Response {

    class Errors(values: List<Error>) : List<Errors.Error> by values, Response() {
        class Error(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail> = emptyList()
        ) {
            class Detail(

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String?,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
            )
        }
    }

    class Incident(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: String,
        @field:JsonProperty("level") @param:JsonProperty("level") val level: String,
        @field:JsonProperty("service") @param:JsonProperty("service") val service: Service,
        @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
    ) : Response() {

        class Service(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("version") @param:JsonProperty("version") val version: String
        )

        class Detail(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("metadata") @param:JsonProperty("metadata") val metadata: JsonNode
        )
    }
}
