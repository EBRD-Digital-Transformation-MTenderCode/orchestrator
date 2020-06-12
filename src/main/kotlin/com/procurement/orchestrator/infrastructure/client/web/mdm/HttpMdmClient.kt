package com.procurement.orchestrator.infrastructure.client.web.mdm

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.RestClient
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.*
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import com.procurement.orchestrator.infrastructure.model.Version
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.net.URL

class HttpMdmClient(
    private val errorDescriptionRepository: ErrorDescriptionRepository?,
    private val restClient: RestClient,
    private val transform: Transform,
    properties: ComponentProperties.Component
) : MdmClient {

    private val baseUrl: URL = URL(properties.url)

    override suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> =

        errorDescriptionRepository!!.load(codes = params.codes, language = params.language.toUpperCase())
            .orForwardFail { fail -> return fail }
            .map { error ->
                GetErrorDescriptionsAction.Result.Error(
                    code = error.code,
                    description = error.description
                )
            }
            .let { errors ->
                Reply.Success(
                    id = EMPTY_REPLY_ID,
                    version = Version.parse("2.0.0"),
                    result = Option.pure(GetErrorDescriptionsAction.Result(errors))
                )
            }
            .asSuccess()

    override suspend fun enrichCountry(
        id: CommandId,
        params: EnrichCountryAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<GetCountry.Result, Fail.Incident>
    ): Result<GetCountry.Result, Fail.Incident> {

        val url: URL = getCountryEndpoint(params.countyId)
        val httpUrlBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()

        val queryParams = mapOf(
            "lang" to params.lang,
            "scheme" to params.scheme
        )
        queryParams.forEach { key, value ->
            httpUrlBuilder.addQueryParameter(name = key, value = value)
        }

        val response = restClient.call(url = httpUrlBuilder.build())
            .orForwardFail { error -> return error }

        return handler(response, transform)
    }

    private fun getCountryEndpoint(countyId: String): URL =
        URL("$baseUrl/addresses/countries/${countyId}")


    override suspend fun enrichRegion(
        id: CommandId,
        params: EnrichRegionAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<GetRegion.Result, Fail.Incident>
    ): Result<GetRegion.Result, Fail.Incident> {

        val url: URL = getRegionEndpoint(params.countyId, params.regionId)
        val httpUrlBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()

        val queryParams = mapOf(
            "lang" to params.lang,
            "scheme" to params.scheme
        )
        queryParams.forEach { key, value ->
            httpUrlBuilder.addQueryParameter(name = key, value = value)
        }

        val response = restClient.call(url = httpUrlBuilder.build())
            .orForwardFail { error -> return error }

        return handler(response, transform)
    }

    private fun getRegionEndpoint(countyId: String, regionId: String): URL =
        URL("$baseUrl/addresses/countries/${countyId}/regions/${regionId}")


    override suspend fun enrichLocality(
        id: CommandId,
        params: EnrichLocalityAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<GetLocality.Result, Fail.Incident>
    ): Result<GetLocality.Result, Fail.Incident> {

        val url: URL = getLocalityEndpoint(params.countyId, params.regionId, params.localityId)
        val httpUrlBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()

        val queryParams = mapOf(
            "lang" to params.lang,
            "scheme" to params.scheme
        )
        queryParams.forEach { key, value ->
            httpUrlBuilder.addQueryParameter(name = key, value = value)
        }

        val response = restClient.call(url = httpUrlBuilder.build())
            .orForwardFail { error -> return error }

        return handler(response, transform)
    }

    private fun getLocalityEndpoint(countyId: String, regionId: String, localityId: String): URL =
        URL("$baseUrl/addresses/countries/${countyId}/regions/${regionId}/localities/${localityId}")
}
