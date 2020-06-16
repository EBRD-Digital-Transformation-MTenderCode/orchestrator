package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

abstract class EnrichLocalityAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
        @field:JsonProperty("countryId") @param:JsonProperty("countryId") val countryId: String,
        @field:JsonProperty("regionId") @param:JsonProperty("regionId") val regionId: String,
        @field:JsonProperty("localityId") @param:JsonProperty("localityId") val localityId: String
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

object GetLocality{

    const val CODE_SCHEME_NOT_FOUND: String = "404.20.13.03"
    const val CODE_ID_NOT_FOUND: String = "404.20.13.02"
    const val CODE_TRANSLATION_NOT_FOUND: String = "404.20.13.05"
    const val CODE_LANGUAGE_NOT_FOUND: String = "400.20.02.01"

    sealed class Result {
        data class Success (val id: String, val scheme: String, val description: String, val uri: String) : Result()

        sealed class Fail : GetLocality.Result() {
            data class IdNotFound (val details: EnrichLocalityAction.Response.Error) : Fail()
            data class TranslationNotFound (val details: EnrichLocalityAction.Response.Error) : Fail()
            data class LanguageNotFound (val details: EnrichLocalityAction.Response.Error) : Fail()
            object SchemeNotFound : Fail()
        }
    }
}