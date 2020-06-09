package com.procurement.orchestrator.infrastructure.client.web.mdm.action

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

abstract class EnrichLocalityAction {

    class Params(
        @field:JsonProperty("lang") @param:JsonProperty("lang") val lang: String,
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
        @field:JsonProperty("countyId") @param:JsonProperty("countyId") val countyId: String,
        @field:JsonProperty("regionId") @param:JsonProperty("regionId") val regionId: String,
        @field:JsonProperty("localityId") @param:JsonProperty("localityId") val localityId: String
    )

    class Result(
        @field:JsonProperty("data") @param:JsonProperty("data") val data: Data
    ) : Serializable {
        data class Data(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
        ) : Serializable
    }

    class ResponseError(
        @field:JsonProperty("errors") @param:JsonProperty("errors") val errors: List<Error>
    ) : Serializable {
        data class Error(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String
        ) : Serializable
    }
}
