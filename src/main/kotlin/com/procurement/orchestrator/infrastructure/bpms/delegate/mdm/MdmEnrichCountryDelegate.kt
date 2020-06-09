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
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import org.springframework.stereotype.Component

@Component
class MdmEnrichCountryDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractRestDelegate<MdmEnrichCountryDelegate.Parameters, List<EnrichCountryAction.Result>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
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
    ): Result<List<EnrichCountryAction.Result>, Fail.Incident> {

        val requestInfo = context.requestInfo

        val submissions = context.tryGetSubmissions()
            .orForwardFail { error -> return error }

        val countriesFromContext = submissions.details
            .asSequence()
            .flatMap { submission -> submission.candidates.asSequence() }
            .map { candidate -> candidate.defineCountryInfoByLocation(parameters.location) }
            .toSet()

        val results = countriesFromContext.map { country ->
            val response = mdmClient.enrichCountry(
                id = commandId,
                params = getParams(requestInfo.language, country),
                handler = ::processResponse
            ).orForwardFail { error -> return error }

            resolveResponseEvent(response, executionInterceptor)
        }

        return results.asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: List<EnrichCountryAction.Result>
    ): MaybeFail<Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        val enrichedCountries = result.map { it.data }

        val updatedSubmissions = submissions.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> updateCountry(candidate, enrichedCountries) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(Details(updatedSubmissions))

        return MaybeFail.none()
    }


    private fun updateCountry(
        candidate: Organization,
        enrichedCountries: List<EnrichCountryAction.Result.Data>
    ): Organization {
        val country = candidate.address!!.addressDetails!!.country
        val enrichedCountry = enrichedCountries
            .find { it.id == country.id && it.scheme == country.scheme }!!
        val updatedCountry = country.copy(
            description = enrichedCountry.description,
            uri = enrichedCountry.uri
        )
        return candidate
            .copy(address = candidate.address
                .copy(addressDetails = candidate.address.addressDetails!!
                    .copy(country = updatedCountry)
                )
            )
    }

    private fun getParams(language: String, country: Country): EnrichCountryAction.Params =
        EnrichCountryAction.Params(
            lang = language,
            scheme = country.scheme,
            countyId = country.id
        )

    private fun Organization.defineCountryInfoByLocation(location: Location) =
        when(location) {
            Location.SUBMISSION -> {
                val country = this.address!!.addressDetails!!.country
                Country(id = country.id, scheme = country.scheme)
            }
        }

    private fun resolveResponseEvent(response: ResponseEvent, executionInterceptor: ExecutionInterceptor)
        : EnrichCountryAction.Result =
        when (response) {
            is ResponseEvent.Success        -> response.value
            is ResponseEvent.Fail     -> {
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

    fun processResponse(response: CallResponse, transform: Transform): Result<ResponseEvent, Fail.Incident> {
        val responseContent = response.content
        return when (response.code) {
            HTTP_CODE_200 -> {
                val result = transform.tryDeserialization(
                    value = responseContent,
                    target = EnrichCountryAction.Result::class.java
                )
                    .orReturnFail { fail ->
                        return failure(
                            Fail.Incident.BadResponse(description = fail.description, body = responseContent)
                        )
                    }
                ResponseEvent.Success(value = result)
                    .asSuccess()
            }

            HTTP_CODE_404 -> {
                val responseError = transform.tryDeserialization(
                    value = responseContent,
                    target = EnrichCountryAction.ResponseError::class.java
                )
                    .orForwardFail { error -> return error }
                success(ResponseEvent.Fail(responseError))
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

    sealed class ResponseEvent {
        data class Success (val value: EnrichCountryAction.Result)          : ResponseEvent()
        data class Fail    (val details: EnrichCountryAction.ResponseError) : ResponseEvent()
    }

    data class Parameters(val location: Location)
    data class Country(val id: String, val scheme: String)
}

