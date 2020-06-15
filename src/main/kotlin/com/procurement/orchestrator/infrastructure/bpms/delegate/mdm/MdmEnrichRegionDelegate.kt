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
import com.procurement.orchestrator.domain.model.address.region.RegionDetails
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegion
import org.springframework.stereotype.Component

@Component
class MdmEnrichRegionDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractRestDelegate<MdmEnrichRegionDelegate.Parameters, List<RegionDetails>>(
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
    ): Result<List<RegionDetails>, Fail.Incident> {

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
                    .enrichRegion(params = getParams(requestInfo.language, country))
                    .orForwardFail { error -> return error }

                handleResult(result, executionInterceptor)
            }

        return results.asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        result: List<RegionDetails>
    ): MaybeFail<Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orReturnFail { error -> return MaybeFail.fail(error) }

        val regiones = result.associateBy { it }

        val updatedSubmissions = submissions.details
            .map { submission ->
                val updatedCandidates = submission.candidates
                    .map { candidate -> candidate.updateRegion(regiones) }
                submission.copy(candidates = Candidates(updatedCandidates))
            }

        context.submissions = Submissions(Details(updatedSubmissions))

        return MaybeFail.none()
    }

    private fun Organization.updateRegion(
        enrichedRegionesById: Map<RegionDetails, RegionDetails>
    ): Organization {
        val oldRegion = this.address!!.addressDetails!!.region
        val enrichedRegion = enrichedRegionesById.getValue(oldRegion)
        return this
            .copy(
                address = this.address
                    .copy(
                        addressDetails = this.address.addressDetails!!
                            .copy(region = enrichedRegion)
                    )
            )
    }

    private fun getParams(language: String, address: Address): EnrichRegionAction.Params =
        EnrichRegionAction.Params(
            lang = language,
            scheme = address.scheme,
            countyId = address.countryId,
            regionId = address.regionId
        )

    private fun Organization.defineAddressInfoByLocation(location: Location) =
        when (location) {
            Location.SUBMISSION -> {
                val country = this.address!!.addressDetails!!.country
                val region = this.address.addressDetails!!.region
                Address(countryId = country.id, regionId = region.id, scheme = region.scheme)
            }
        }

    private fun handleResult(
        response: GetRegion.Result,
        executionInterceptor: ExecutionInterceptor
    ): RegionDetails = when (response) {
        is GetRegion.Result.Success -> RegionDetails(
            id = response.id,
            scheme = response.scheme,
            description = response.description,
            uri = response.uri
        )
        is GetRegion.Result.Fail    -> {
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
    private data class Address(val countryId: String, val regionId: String, val scheme: String)
}

