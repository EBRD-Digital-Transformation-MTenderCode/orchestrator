package com.procurement.orchestrator.infrastructure.client.web.mdm

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.RestClient
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCountry
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegion
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroups
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroupsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import com.procurement.orchestrator.infrastructure.model.Version
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL

class HttpMdmClient(
    private val errorDescriptionRepository: ErrorDescriptionRepository,
    private val restClient: RestClient,
    private val transform: Transform,
    properties: ComponentProperties.Component
) : MdmClient {

    private val baseUrl: URL = URL(properties.url)

    override suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> =

        errorDescriptionRepository.load(codes = params.codes, language = params.language.toUpperCase())
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

    override suspend fun enrichCountry(params: EnrichCountryAction.Params): Result<GetCountry.Result, Fail.Incident> {

        val httpUrl: HttpUrl = getCountryEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetCountryResponse(response, transform)
    }

    private fun getCountryEndpoint(params: EnrichCountryAction.Params): String = "$baseUrl/addresses/countries/${params.countryId}"

    override suspend fun enrichRegion(params: EnrichRegionAction.Params): Result<GetRegion.Result, Fail.Incident> {

        val httpUrl: HttpUrl = getRegionEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetRegionResponse(response, transform)
    }

    private fun getRegionEndpoint(params: EnrichRegionAction.Params): String =
        "$baseUrl/addresses/countries/${params.countryId}/regions/${params.regionId}"

    override suspend fun enrichLocality(params: EnrichLocalityAction.Params): Result<GetLocality.Result, Fail.Incident> {

        val httpUrl: HttpUrl = getLocalityEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetLocalityResponse(response, transform)
    }

    private fun getLocalityEndpoint(params: EnrichLocalityAction.Params): String =
        "$baseUrl/addresses/countries/${params.countryId}/regions/${params.regionId}/localities/${params.localityId}"


    override suspend fun getRequirementGroups(params: GetRequirementGroupsAction.Params): Result<GetRequirementGroups.Result.Success, Fail.Incident> {

        val httpUrl: HttpUrl = getRequirementGroupsEndpoint()
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("country", params.country)
                addQueryParameter("pmd", params.pmd.toString())
                addQueryParameter("phase", params.phase.toString())
                addQueryParameter("criterionId", params.criterionId)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetRequirementGroups(response, transform)
    }

    private fun getRequirementGroupsEndpoint(): String = "$baseUrl/requirementGroups"


    private fun processGetCountryResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetCountry.Result, Fail.Incident> = when (response.code) {
        AbstractBatchRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichCountryAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetCountry.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        AbstractBatchRestDelegate.HTTP_CODE_400,
        AbstractBatchRestDelegate.HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichCountryAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    GetCountry.ERROR_NO_TRANSLATION_FOR_LANGUAGE ->
                        success(GetCountry.Result.Fail.NoTranslationFounded(errors = responseError.errors.convertGetCountryErrors()))
                    else                                                       ->
                        success(GetCountry.Result.Fail.AnotherError(errors = responseError.errors.convertGetCountryErrors()))
                }
            }

        else                               -> failure(
            Fail.Incident.BadResponse(
                description = "Invalid response code.",
                body = response.content
            )
        )
    }

    private fun List<EnrichCountryAction.Response.Error.Details>.convertGetCountryErrors(): List<GetCountry.Result.Fail.Details> =
        this.map { error ->
            GetCountry.Result.Fail.Details(
                code = error.code,
                description = error.description
            )
        }

    private fun processGetRegionResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetRegion.Result, Fail.Incident> = when (response.code) {
        AbstractBatchRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichRegionAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetRegion.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        AbstractBatchRestDelegate.HTTP_CODE_400,
        AbstractBatchRestDelegate.HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichRegionAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when(error.code) {
                    GetRegion.ERROR_NO_TRANSLATION_FOR_LANGUAGE ->
                        success(GetRegion.Result.Fail.TranslationNotFound(errors = responseError.errors.convertGetRegionErrors()))
                    else                                                       ->
                        success(GetRegion.Result.Fail.AnotherError(errors = responseError.errors.convertGetRegionErrors()))
                }
            }

        else                               -> failure(
            Fail.Incident.BadResponse(
                description = "Invalid response code.",
                body = response.content
            )
        )
    }

    private fun List<EnrichRegionAction.Response.Error.Details>.convertGetRegionErrors(): List<GetRegion.Result.Fail.Details> =
        this.map { error ->
            GetRegion.Result.Fail.Details(
                code = error.code,
                description = error.description
            )
        }

    private fun processGetLocalityResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetLocality.Result, Fail.Incident> = when (response.code) {
        AbstractBatchRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichLocalityAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetLocality.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        AbstractBatchRestDelegate.HTTP_CODE_400,
        AbstractBatchRestDelegate.HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichLocalityAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    GetLocality.CODE_SCHEME_NOT_FOUND ->
                        success(GetLocality.Result.Fail.SchemeNotFound)
                    GetLocality.CODE_ID_NOT_FOUND     ->
                        success(GetLocality.Result.Fail.IdNotFound(responseError))
                    GetLocality.CODE_TRANSLATION_NOT_FOUND     ->
                        success(GetLocality.Result.Fail.TranslationNotFound(responseError))
                    GetLocality.CODE_LANGUAGE_NOT_FOUND     ->
                        success(GetLocality.Result.Fail.LanguageNotFound(responseError))
                    else                                                ->
                        failure(Fail.Incident.BadResponse(description = "Unknown error code.", body = response.content))
                }
            }

        else  ->  failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetRequirementGroups(
        response: CallResponse,
        transform: Transform
    ): Result<GetRequirementGroups.Result.Success, Fail.Incident> = when (response.code) {
        AbstractBatchRestDelegate.HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = GetRequirementGroupsAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetRequirementGroups.Result.Success(
                    requirementGroups = result.data
                        ?.map { requirementResponse ->
                            GetRequirementGroups.Result.Success.RequirementGroup(
                                id = requirementResponse.id,
                                description = requirementResponse.description
                            )
                        }
                        .orEmpty()
                )
            }
            .asSuccess()

        else  ->  failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

}
