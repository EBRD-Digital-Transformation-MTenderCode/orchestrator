package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
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
import com.procurement.orchestrator.domain.model.address.country.CountryDetails
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetCountry
import org.springframework.stereotype.Component

@Component
class MdmEnrichCountryDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractRestDelegate<MdmEnrichCountryDelegate.Parameters, List<CountryDetails>>(
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
        parameters: Parameters,
        context: CamundaGlobalContext,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<CountryDetails>, Fail.Incident> {

        val requestInfo = context.requestInfo

        val submissions = context.tryGetSubmissions()
            .orForwardFail { error -> return error }

        val results = submissions.details
            .asSequence()
            .flatMap { submission -> submission.candidates.asSequence() }
            .map { candidate -> candidate.defineAddressInfoByLocation(parameters.location) }
            .toSet()
            .map { country ->
                val result = mdmClient
                    .enrichCountry(params = getParams(requestInfo.language, country))
                    .orForwardFail { error -> return error }

                handleResult(result, executionInterceptor)
            }

        return results.asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: List<CountryDetails>
    ): MaybeFail<Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        val countries = result.associateBy { it }

        val updatedSubmissions = submissions.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> candidate.updateCountry(countries) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(Details(updatedSubmissions))

        return MaybeFail.none()
    }

    private fun Organization.updateCountry(
        enrichedCountriesById: Map<CountryDetails, CountryDetails>
    ): Organization {
        val oldCountry = this.address!!.addressDetails!!.country
        val enrichedCountry = enrichedCountriesById.getValue(oldCountry)
        return this
            .copy(
                address = this.address
                    .copy(
                        addressDetails = this.address.addressDetails!!
                            .copy(country = enrichedCountry)
                    )
            )
    }

    private fun getParams(language: String, address: Address): EnrichCountryAction.Params =
        EnrichCountryAction.Params(
            lang = language,
            scheme = address.scheme,
            countyId = address.id
        )

    private fun Organization.defineAddressInfoByLocation(location: Location) =
        when (location) {
            Location.SUBMISSION -> {
                val country = this.address!!.addressDetails!!.country
                Address(id = country.id, scheme = country.scheme)
            }
        }

    private fun handleResult(
        response: GetCountry.Result,
        executionInterceptor: ExecutionInterceptor
    ): CountryDetails = when (response) {
        is GetCountry.Result.Success -> CountryDetails(
            id = response.id,
            description = response.description,
            scheme = response.scheme,
            uri = response.uri
        )
        is GetCountry.Result.Fail    -> {
            val errors = response.errors
                .map { error ->
                    Errors.Error(
                        code = error.code,
                        description = error.description
                    )
                }
            executionInterceptor.throwError(errors = errors)
        }
    }

    enum class Location(@JsonValue override val key: String) : EnumElementProvider.Key {

        SUBMISSION("submission");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = orThrow(name)
        }
    }

    data class Parameters(val location: Location)
    private data class Address(val id: String, val scheme: String)
}

