package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import org.springframework.stereotype.Component

@Component
class MdmEnrichLocalityDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractRestDelegate<MdmEnrichLocalityDelegate.Parameters, List<EnrichLocalityAction.Response.Success>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        const val RESPONSE_SCHEME_NOT_FOUND: String = "schemeNotFound"
        const val RESPONSE_ID_NOT_FOUND: String = "idNotFound"

        const val PARAMETER_NAME_LOCATION: String = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let { location ->
                Location.orNull(location)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            actualValue = location,
                            expectedValues = Location.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(location))
    }

    override suspend fun execute(
        commandId: CommandId,
        parameters: Parameters,
        context: CamundaGlobalContext,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<EnrichLocalityAction.Response.Success>, Fail.Incident> {

        val requestInfo = context.requestInfo

        val submissions = context.tryGetSubmissions()
            .orForwardFail { error -> return error }

        val countriesFromContext = submissions.details
            .asSequence()
            .flatMap { submission -> submission.candidates.asSequence() }
            .map { candidate -> candidate.defineCountryInfoByLocation(parameters.location) }
            .toSet()

        val results = countriesFromContext.map { country ->
            val response = mdmClient.enrichLocality(
                id = commandId,
                params = getParams(requestInfo.language, country),
                handler = ::processResponse
            ).orForwardFail { error -> return error }

            resolveResponseEvent(response, executionInterceptor)
        }
            .filter { optionalResult -> optionalResult.isDefined }
            .map { result -> result.get }

        return results.asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: List<EnrichLocalityAction.Response.Success>
    ): MaybeFail<Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        val enrichedCountries = result.map { it.data }

        val updatedSubmissions = submissions.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> updateLocality(candidate, enrichedCountries) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(Details(updatedSubmissions))

        return MaybeFail.none()
    }


    private fun updateLocality(
        candidate: Organization,
        enrichedLocalities: List<EnrichLocalityAction.Response.Success.Data>
    ): Organization {
        val locality = candidate.address!!.addressDetails!!.locality
        val enrichedLocality = enrichedLocalities
            .find { it.id == locality.id && it.scheme == locality.scheme }!!
        val updatedLocality = locality.copy(
            description = enrichedLocality.description,
            uri = enrichedLocality.uri
        )
        return candidate
            .copy(address = candidate.address
                .copy(addressDetails = candidate.address.addressDetails!!
                    .copy(locality = updatedLocality)
                )
            )
    }

    private fun getParams(language: String, country: Country): EnrichLocalityAction.Params =
        EnrichLocalityAction.Params(
            lang = language,
            scheme = country.scheme,
            countyId = country.id,
            regionId = country.regionId,
            localityId = country.localityId
        )

    private fun Organization.defineCountryInfoByLocation(location: Location) =
        when(location) {
            Location.SUBMISSION -> {
                val country = this.address!!.addressDetails!!.country
                val region = this.address.addressDetails!!.region
                val locality = this.address.addressDetails.locality
                Country(id = country.id, regionId = region.id, scheme = region.scheme, localityId = locality.id)
            }
        }

    private fun resolveResponseEvent(response: GetLocality.Result, executionInterceptor: ExecutionInterceptor)
        : Option<EnrichLocalityAction.Response.Success> =
        when (response) {
            is GetLocality.Result.Success             -> Option.pure(
                EnrichLocalityAction.Response.Success(
                    data = EnrichLocalityAction.Response.Success.Data(
                        id = response.id,
                        description = response.description,
                        scheme = response.scheme,
                        uri = response.uri
                    )
                )
            )
            is GetLocality.Result.Fail.SchemeNotFound -> Option.none()
            is GetLocality.Result.Fail.IdNotFound     -> {
                val errors = response.details.errors
                    .map { error ->
                        Errors.Error(
                            code = error.code,
                            description = error.description
                        )
                    }
                executionInterceptor.throwError(errors = errors)
            }
        }

    fun processResponse(response: CallResponse, transform: Transform): Result<GetLocality.Result, Fail.Incident> {
        val responseContent = response.content
        return when (response.code) {
            HTTP_CODE_200 -> {
                val result = transform.tryDeserialization(
                    value = responseContent,
                    target = EnrichLocalityAction.Response.Success::class.java
                )
                    .orReturnFail { fail ->
                        return failure(
                            Fail.Incident.BadResponse(description = fail.description, body = responseContent)
                        )
                    }
                GetLocality.Result.Success(
                    id = result.data.id,
                    description = result.data.description,
                    uri = result.data.uri,
                    scheme = result.data.scheme
                )
                    .asSuccess()
            }

            HTTP_CODE_404 -> {
                val responseError = transform.tryDeserialization(
                    value = responseContent,
                    target = EnrichLocalityAction.Response.Error::class.java
                )
                    .orForwardFail { error -> return error }
                val error = responseError.errors.first()
                when (error.code) {
                    RESPONSE_SCHEME_NOT_FOUND -> success(GetLocality.Result.Fail.SchemeNotFound)
                    RESPONSE_ID_NOT_FOUND     -> success(GetLocality.Result.Fail.IdNotFound(responseError))
                    else                      ->
                        failure(Fail.Incident.BadResponse(description = "Unknown error code.", body = responseContent))
                }
            }
            else          -> failure(
                Fail.Incident.BadResponse(
                    description = "Invalid response code.",
                    body = responseContent
                )
            )
        }
    }


    enum class Location(@JsonValue override val key: String) : EnumElementProvider.Key {

        SUBMISSION("submission");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Location.orThrow(name)
        }
    }

    data class Parameters(val location: Location)
    data class Country(val id: String, val regionId: String, val localityId: String, val scheme: String)
}

