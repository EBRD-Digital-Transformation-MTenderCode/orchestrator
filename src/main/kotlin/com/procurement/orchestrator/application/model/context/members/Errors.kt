package com.procurement.orchestrator.application.model.context.members

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class Errors(values: List<Error>) : List<Errors.Error> by values, Serializable {

    data class Error(
        @field:JsonProperty("code") @param:JsonProperty("code") val code: String,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String = "",

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail> = emptyList()
    ) : Serializable {

        data class Detail(
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
        ) : Serializable
    }
}
