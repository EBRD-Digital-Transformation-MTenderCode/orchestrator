package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.address.locality.LocalityDetails
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractBatchRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetLocality
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.springframework.stereotype.Component

@Component
class MdmEnrichLocalityDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractBatchRestDelegate<MdmEnrichLocalityDelegate.Parameters, EnrichLocalityAction.Params, List<LocalityDetails>>(
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

    override fun prepareSeq(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<List<EnrichLocalityAction.Params>, Fail.Incident> {
        val requestInfo = context.requestInfo

        val submissions = context.tryGetSubmissions()
            .orForwardFail { error -> return error }

        return  submissions.details
            .asSequence()
            .flatMap { submission -> submission.candidates.asSequence() }
            .map { candidate -> candidate.defineAddressInfoByLocation(parameters.location) }
            .toSet()
            .map { country -> getParams(requestInfo.language, country) }
            .asSuccess()
    }

    override suspend fun execute(
        elements: List<EnrichLocalityAction.Params>,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<LocalityDetails>, Fail.Incident> {

        return elements
            .map { params ->
                val response = mdmClient
                    .enrichLocality(params = params)
                    .orForwardFail { error -> return error }

                handleResult(response, executionInterceptor)
            }
            .filter { optionalResult -> optionalResult.isDefined }
            .map { result -> result.get }
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: List<LocalityDetails>
    ): MaybeFail<Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        val localities = result.associateBy { it }

        val updatedSubmissions = submissions.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> candidate.updateLocality(localities) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(Details(updatedSubmissions))

        return MaybeFail.none()
    }


    private fun Organization.updateLocality(
        enrichedLocalitiesById: Map<LocalityDetails, LocalityDetails>
    ): Organization {
        val oldLocality = this.address!!.addressDetails!!.locality
        val enrichedLocality = enrichedLocalitiesById[oldLocality] ?: oldLocality
        return this
            .copy(address = this.address
                .copy(addressDetails = this.address.addressDetails!!
                    .copy(locality = enrichedLocality)
                )
            )
    }

    private fun getParams(language: String, address: Address): EnrichLocalityAction.Params =
        EnrichLocalityAction.Params(
            lang = language,
            scheme = address.scheme,
            countryId = address.countryId,
            regionId = address.regionId,
            localityId = address.localityId
        )

    private fun Organization.defineAddressInfoByLocation(location: Location) =
        when (location) {
            Location.SUBMISSION -> {
                val country = this.address!!.addressDetails!!.country
                val region = this.address.addressDetails!!.region
                val locality = this.address.addressDetails.locality
                Address(countryId = country.id, regionId = region.id, scheme = locality.scheme, localityId = locality.id)
            }
        }

    private fun handleResult(
        result: GetLocality.Result,
        executionInterceptor: ExecutionInterceptor
    ): Option<LocalityDetails> = when (result) {
        is GetLocality.Result.Success             -> Option.pure(
            LocalityDetails(
                id = result.id,
                scheme = result.scheme,
                description = result.description,
                uri = result.uri
            )
        )
        is GetLocality.Result.Fail.SchemeNotFound -> Option.none()
        is GetLocality.Result.Fail.IdNotFound     -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetLocality.Result.Fail.LanguageNotFound -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetLocality.Result.Fail.AnotherError        -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
        is GetLocality.Result.Fail.TranslationNotFound -> {
            executionInterceptor.throwIncident(
                Incident(
                    id = executionInterceptor.processInstanceId,
                    date = nowDefaultUTC().format(),
                    level = Fail.Incident.Level.ERROR.toString(),
                    service = GlobalProperties.service
                        .let { service ->
                            Incident.Service(
                                id = service.id,
                                name = service.name,
                                version = service.version
                            )
                        },
                    details = result.details.errors
                        .map { incident ->
                            Incident.Detail(
                                code = incident.code,
                                description = incident.description
                            )
                        }
                )
            )
        }
    }

    private fun List<EnrichLocalityAction.Response.Error.Details>.convertErrors(): List<Errors.Error> =
        this.map { error ->
            Errors.Error(
                code = error.code,
                description = error.description
            )
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
    private data class Address(val countryId: String, val regionId: String, val localityId: String, val scheme: String)
}

