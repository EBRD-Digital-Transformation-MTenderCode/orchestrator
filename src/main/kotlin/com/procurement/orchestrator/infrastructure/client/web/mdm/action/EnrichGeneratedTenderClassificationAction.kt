package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.tender.ProcurementCategory
import java.io.Serializable

abstract class EnrichGeneratedTenderClassificationAction {

    class Params(
        @field:JsonProperty("classificationId") @param:JsonProperty("classificationId") val classificationId: String,
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
    )

    sealed class Response {

        class Success(
            @field:JsonProperty("data") @param:JsonProperty("data") val data: Data
        ) : Response(), Serializable {

            data class Data(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String
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

object GetGeneratedTenderClassification {

    const val CODE_LANGUAGE_NOT_FOUND: String = "404.20.17.01"
    const val CODE_SCHEME_NOT_FOUND: String = "404.20.17.02"
    const val CODE_ID_NOT_FOUND: String = "404.20.17.03"
    const val CODE_TRANSLATION_NOT_FOUND: String = "404.20.17.04"

    sealed class Result {
        data class Success(val id: String, val scheme: String, val description: String) : Result()

        sealed class Fail : GetGeneratedTenderClassification.Result() {
            data class IdNotFound(val details: EnrichGeneratedTenderClassificationAction.Response.Error) : Fail()
            data class TranslationNotFound(val details: EnrichGeneratedTenderClassificationAction.Response.Error) : Fail()
            data class LanguageNotFound(val details: EnrichGeneratedTenderClassificationAction.Response.Error) : Fail()
            data class AnotherError(val details: EnrichGeneratedTenderClassificationAction.Response.Error) : Fail()
            data class SchemeNotFound (val details: EnrichGeneratedTenderClassificationAction.Response.Error) : Fail()
        }
    }
}