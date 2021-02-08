package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

abstract class GetRegistrationSchemesAction {

    class Params(
        @field:JsonProperty("countryId") @param:JsonProperty("countryId") val countryId: List<String>
    )

    sealed class Response {

        class Success(
            @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("data") @param:JsonProperty("data") val data: List<RegistrationScheme>?
        ) : Response(), Serializable {

            data class RegistrationScheme(
                @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
                @param:JsonProperty("schemes") @field:JsonProperty("schemes") val schemes: List<String>
            ) : Serializable
        }

        class Error(
            @field:JsonProperty("errors") @param:JsonProperty("errors") val errors: List<Details>
        ) : Serializable {
            data class Details(
                @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String
            ) : Response(), Serializable
        }
    }
}


object GetOrganizationSchemes {
    sealed class Result {

        data class Success(val registrationSchemes: List<RegistrationScheme>) : Result() {
            data class RegistrationScheme(val country: String, val schemes: List<String>)
        }

        sealed class Fail : Result() {
            data class AnotherError(val details: GetRegistrationSchemesAction.Response.Error) : Fail()
        }
    }
}
