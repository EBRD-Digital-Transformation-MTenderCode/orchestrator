package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

object EnrichCountryAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
        @field:JsonProperty("countryId") @param:JsonProperty("countryId") val countryId: String
    )

    sealed class Response {

        class Success(
            @field:JsonProperty("data") @param:JsonProperty("data") val data: Data
        ) : Response(), Serializable {

            data class Data(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
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

object GetCountry{

    const val ERROR_NO_TRANSLATION_FOR_LANGUAGE = "404.20.11.05"

    sealed class Result {
        data class Success (val id: String, val scheme: String, val description: String, val uri: String) : Result()

        sealed class Fail(val errors: List<Details>) : Result() {
            class AnotherError(errors: List<Details>): Fail(errors)
            class NoTranslationFounded(errors: List<Details>) : Fail(errors)

            class Details(val code: String, val description: String)
        }
    }
}
