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
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.RestClient
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichClassificationsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichGeneratedTenderClassificationAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichUnitsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetClassification
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCountry
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCriteria
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetGeneratedTenderClassification
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetOrganizationSchemes
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegion
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegistrationSchemesAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroups
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementGroupsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirements
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRequirementsAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetStandardCriteria
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetStandardCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetUnit
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

    companion object {
        const val HTTP_CODE_200: Int = 200
        const val HTTP_CODE_400: Int = 400
        const val HTTP_CODE_404: Int = 404
    }

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

    private fun getCountryEndpoint(params: EnrichCountryAction.Params): String =
        "$baseUrl/addresses/countries/${params.countryId}"

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

    override suspend fun getCriteria(params: GetCriteriaAction.Params): Result<GetCriteria.Result.Success, Fail.Incident> {

        val httpUrl: HttpUrl = getCriteriaEndpoint()
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("country", params.country)
                addQueryParameter("pmd", params.pmd.toString())
                addQueryParameter("phase", params.phase.toString())
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetCriteria(response, transform)
    }

    private fun getCriteriaEndpoint(): String = "$baseUrl/criteria"

    override suspend fun getStandardCriteria(params: GetStandardCriteriaAction.Params): Result<GetStandardCriteria.Result.Success, Fail.Incident> {
        val httpUrl: HttpUrl = getStandardCriteriaEndpoint()
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("country", params.country)
                params.mainProcurementCategory?.let { addQueryParameter("mainProcurementCategory", it.toString()) }
                params.criteriaCategory?.let { addQueryParameter("criteriaCategory", it) }
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetStandardCriteria(response, transform)
    }

    private fun getStandardCriteriaEndpoint(): String = "$baseUrl/standardCriteria"

    override suspend fun getOrganizationSchemes(params: GetRegistrationSchemesAction.Params): Result<GetOrganizationSchemes.Result, Fail.Incident> {
        val IdsQueryParam = params.countryId.joinToString(separator = ",")

        val httpUrl: HttpUrl = getOrganizationSchemesEndpoint()
            .toHttpUrl()
            .newBuilder()
            .apply {
                addEncodedQueryParameter("countryId", IdsQueryParam)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetOrganizationSchemes(response, transform)
    }

    private fun getOrganizationSchemesEndpoint(): String = "$baseUrl/organization/schemes"

    override suspend fun getRequirements(params: GetRequirementsAction.Params): Result<GetRequirements.Result.Success, Fail.Incident> {

        val httpUrl: HttpUrl = getRequirementsEndpoint()
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("country", params.country)
                addQueryParameter("pmd", params.pmd.toString())
                addQueryParameter("phase", params.phase.toString())
                addQueryParameter("requirementGroupId", params.requirementGroupId)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetRequirements(response, transform)
    }

    private fun getRequirementsEndpoint(): String = "$baseUrl//requirements"

    override suspend fun enrichClassifications(params: EnrichClassificationsAction.Params): Result<GetClassification.Result, Fail.Incident> {
        val httpUrl: HttpUrl = getClassificationEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetClassificationResponse(response, transform)
    }

    private fun getClassificationEndpoint(params: EnrichClassificationsAction.Params): String =
        "$baseUrl/classifications/${params.classificationId}"

    override suspend fun enrichUnits(params: EnrichUnitsAction.Params): Result<GetUnit.Result, Fail.Incident> {
        val httpUrl: HttpUrl = getUnitEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetUnitResponse(response, transform)
    }

    private fun getUnitEndpoint(params: EnrichUnitsAction.Params): String =
        "$baseUrl/units/${params.unitId}"

    override suspend fun enrichGeneratedTenderClassification(params: EnrichGeneratedTenderClassificationAction.Params): Result<GetGeneratedTenderClassification.Result, Fail.Incident> {
        val httpUrl: HttpUrl = getGeneratedTenderClassificationEndpoint(params)
            .toHttpUrl()
            .newBuilder()
            .apply {
                addQueryParameter("lang", params.lang)
                addQueryParameter("scheme", params.scheme)
            }
            .build()

        val response = restClient.call(url = httpUrl)
            .orForwardFail { error -> return error }

        return processGetGeneratedTenderClassificationResponse(response, transform)
    }

    private fun getGeneratedTenderClassificationEndpoint(params: EnrichGeneratedTenderClassificationAction.Params): String =
        "$baseUrl/classifications/${params.classificationId}"

    private fun processGetCountryResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetCountry.Result, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
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

        HTTP_CODE_400,
        HTTP_CODE_404 -> transform
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
                    else ->
                        success(GetCountry.Result.Fail.AnotherError(errors = responseError.errors.convertGetCountryErrors()))
                }
            }

        else -> failure(
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
        HTTP_CODE_200 -> transform
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

        HTTP_CODE_400,
        HTTP_CODE_404 -> transform
            .tryDeserialization(
                value = response.content,
                target = EnrichRegionAction.Response.Error::class.java
            )
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    GetRegion.ERROR_NO_TRANSLATION_FOR_LANGUAGE ->
                        success(GetRegion.Result.Fail.TranslationNotFound(errors = responseError.errors.convertGetRegionErrors()))
                    else ->
                        success(GetRegion.Result.Fail.AnotherError(errors = responseError.errors.convertGetRegionErrors()))
                }
            }

        else -> failure(
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
        HTTP_CODE_200 -> transform
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

        HTTP_CODE_400,
        HTTP_CODE_404 -> transform
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
                    GetLocality.CODE_ID_NOT_FOUND ->
                        success(GetLocality.Result.Fail.IdNotFound(responseError))
                    GetLocality.CODE_TRANSLATION_NOT_FOUND ->
                        success(GetLocality.Result.Fail.TranslationNotFound(responseError))
                    GetLocality.CODE_LANGUAGE_NOT_FOUND ->
                        success(GetLocality.Result.Fail.LanguageNotFound(responseError))
                    else ->
                        success(GetLocality.Result.Fail.AnotherError(responseError))
                }
            }

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetRequirementGroups(
        response: CallResponse,
        transform: Transform
    ): Result<GetRequirementGroups.Result.Success, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
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

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))
    }

    private fun processGetCriteria(
        response: CallResponse,
        transform: Transform
    ): Result<GetCriteria.Result.Success, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = GetCriteriaAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetCriteria.Result.Success(
                    criteria = result.data
                        ?.map { criterion ->
                            GetCriteria.Result.Success.Criterion(
                                id = criterion.id,
                                title = criterion.title,
                                description = criterion.description,
                                classification = criterion.classification.let { classification ->
                                    GetCriteria.Result.Success.Criterion.Classification(
                                        id = classification.id,
                                        scheme = classification.scheme
                                    )
                                }
                            )
                        }
                        .orEmpty()
                )
            }
            .asSuccess()

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetStandardCriteria(
        response: CallResponse,
        transform: Transform
    ): Result<GetStandardCriteria.Result.Success, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = GetStandardCriteriaAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetStandardCriteria.Result.Success(
                    criteria = result.data
                        ?.map { criterion ->
                            GetStandardCriteria.Result.Success.Criterion(
                                id = criterion.id,
                                title = criterion.title,
                                description = criterion.description,
                                classification = criterion.classification
                                    .let { classification ->
                                        GetStandardCriteria.Result.Success.Criterion.Classification(
                                            id = classification.id,
                                            scheme = classification.scheme
                                        )
                                    },
                                requirementGroups = criterion.requirementGroups
                                    .map { requirementGroup ->
                                        GetStandardCriteria.Result.Success.Criterion.RequirementGroup(
                                            id = requirementGroup.id,
                                            description = requirementGroup.description,
                                            requirements = requirementGroup.requirements
                                                .map { requirement ->
                                                    GetStandardCriteria.Result.Success.Criterion.RequirementGroup.Requirement(
                                                        id = requirement.id,
                                                        description = requirement.description,
                                                        title = requirement.title,
                                                        dataType = requirement.dataType
                                                    )
                                                }
                                        )
                                    }
                            )
                        }
                        .orEmpty()
                )
            }
            .asSuccess()

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetOrganizationSchemes(
        response: CallResponse,
        transform: Transform
    ): Result<GetOrganizationSchemes.Result, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = GetRegistrationSchemesAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetOrganizationSchemes.Result.Success(
                    registrationSchemes = result.data
                        ?.map { registrationScheme ->
                            GetOrganizationSchemes.Result.Success.RegistrationScheme(
                                country = registrationScheme.country,
                                schemes = registrationScheme.schemes
                            )
                        }
                        .orEmpty()
                )
            }
            .asSuccess()

        HTTP_CODE_404 -> transform
            .tryDeserialization(response.content, GetRegistrationSchemesAction.Response.Error::class.java)
            .orForwardFail { error -> return error }
            .let { responseError -> success(GetOrganizationSchemes.Result.Fail.AnotherError(responseError)) }

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetRequirements(
        response: CallResponse,
        transform: Transform
    ): Result<GetRequirements.Result.Success, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(
                value = response.content,
                target = GetRequirementsAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetRequirements.Result.Success(
                    requirements = result.data
                        ?.map { criterion ->
                            GetRequirements.Result.Success.Requirement(
                                id = criterion.id,
                                title = criterion.title,
                                description = criterion.description
                            )
                        }
                        .orEmpty()
                )
            }
            .asSuccess()

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetClassificationResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetClassification.Result, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(response.content, EnrichClassificationsAction.Response.Success::class.java)
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetClassification.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    scheme = result.data.scheme
                )
            }
            .asSuccess()

        HTTP_CODE_400,
        HTTP_CODE_404 -> transform
            .tryDeserialization(response.content, EnrichClassificationsAction.Response.Error::class.java)
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    GetClassification.CODE_SCHEME_NOT_FOUND ->
                        success(GetClassification.Result.Fail.SchemeNotFound)
                    GetClassification.CODE_ID_NOT_FOUND ->
                        success(GetClassification.Result.Fail.IdNotFound(responseError))
                    GetClassification.CODE_TRANSLATION_NOT_FOUND ->
                        success(GetClassification.Result.Fail.TranslationNotFound(responseError))
                    GetClassification.CODE_LANGUAGE_NOT_FOUND ->
                        success(GetClassification.Result.Fail.LanguageNotFound(responseError))
                    else ->
                        success(GetClassification.Result.Fail.AnotherError(responseError))
                }
            }

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetUnitResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetUnit.Result, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(response.content, EnrichUnitsAction.Response.Success::class.java)
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetUnit.Result.Success(id = result.data.id, name = result.data.name)
            }
            .asSuccess()

        HTTP_CODE_400,
        HTTP_CODE_404 -> transform
            .tryDeserialization(response.content, EnrichUnitsAction.Response.Error::class.java)
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    GetUnit.CODE_ID_NOT_FOUND ->
                        success(GetUnit.Result.Fail.IdNotFound(responseError))
                    GetUnit.CODE_TRANSLATION_NOT_FOUND ->
                        success(GetUnit.Result.Fail.TranslationNotFound(responseError))
                    GetUnit.CODE_LANGUAGE_NOT_FOUND ->
                        success(GetUnit.Result.Fail.LanguageNotFound(responseError))
                    else ->
                        success(GetUnit.Result.Fail.AnotherError(responseError))
                }
            }

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))

    }

    private fun processGetGeneratedTenderClassificationResponse(
        response: CallResponse,
        transform: Transform
    ): Result<GetGeneratedTenderClassification.Result, Fail.Incident> = when (response.code) {
        HTTP_CODE_200 -> transform
            .tryDeserialization(
                response.content,
                EnrichGeneratedTenderClassificationAction.Response.Success::class.java
            )
            .orReturnFail { fail ->
                return failure(
                    Fail.Incident.BadResponse(description = fail.description, body = response.content)
                )
            }
            .let { result ->
                GetGeneratedTenderClassification.Result.Success(
                    id = result.data.id,
                    scheme = result.data.scheme,
                    description = result.data.description
                )
            }
            .asSuccess()

        HTTP_CODE_400,
        HTTP_CODE_404 -> transform
            .tryDeserialization(response.content, EnrichGeneratedTenderClassificationAction.Response.Error::class.java)
            .orForwardFail { error -> return error }
            .let { responseError ->
                val error = responseError.errors.first()
                when (error.code) {
                    GetGeneratedTenderClassification.CODE_SCHEME_NOT_FOUND ->
                        success(GetGeneratedTenderClassification.Result.Fail.SchemeNotFound(responseError))
                    GetGeneratedTenderClassification.CODE_ID_NOT_FOUND ->
                        success(GetGeneratedTenderClassification.Result.Fail.IdNotFound(responseError))
                    GetGeneratedTenderClassification.CODE_TRANSLATION_NOT_FOUND ->
                        success(GetGeneratedTenderClassification.Result.Fail.TranslationNotFound(responseError))
                    GetGeneratedTenderClassification.CODE_LANGUAGE_NOT_FOUND ->
                        success(GetGeneratedTenderClassification.Result.Fail.LanguageNotFound(responseError))
                    else ->
                        success(GetGeneratedTenderClassification.Result.Fail.AnotherError(responseError))
                }
            }

        else -> failure(Fail.Incident.BadResponse(description = "Invalid response code.", body = response.content))
    }
}
