package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

abstract class EnrichUnitsAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("unitId") @param:JsonProperty("unitId") val unitId: String
    )

    sealed class Response {

        class Success(
            @field:JsonProperty("data") @param:JsonProperty("data") val data: Data
        ) : Response(), Serializable {

            data class Data(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
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

object GetUnit {

    const val CODE_ID_NOT_FOUND: String = "404.20.18.01"
    const val CODE_LANGUAGE_NOT_FOUND: String = "404.20.18.02"
    const val CODE_TRANSLATION_NOT_FOUND: String = "404.20.18.03"

    sealed class Result {
        data class Success(val id: String, val name: String) : Result()

        sealed class Fail(val details: EnrichUnitsAction.Response.Error) : GetUnit.Result() {
            class IdNotFound(errorDetails: EnrichUnitsAction.Response.Error) : Fail(errorDetails)
            class TranslationNotFound(errorDetails: EnrichUnitsAction.Response.Error) : Fail(errorDetails)
            class LanguageNotFound(errorDetails: EnrichUnitsAction.Response.Error) : Fail(errorDetails)
            class AnotherError(errorDetails: EnrichUnitsAction.Response.Error) : Fail(errorDetails)
        }
    }
}