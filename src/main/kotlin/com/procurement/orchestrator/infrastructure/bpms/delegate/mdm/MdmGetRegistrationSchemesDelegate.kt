package com.procurement.orchestrator.infrastructure.bpms.delegate.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.mdm.Mdm
import com.procurement.orchestrator.domain.model.mdm.ProcessMasterData
import com.procurement.orchestrator.domain.model.scheme.RegistrationScheme
import com.procurement.orchestrator.domain.model.scheme.RegistrationSchemes
import com.procurement.orchestrator.domain.model.scheme.Schemes
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractSingleRestDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetOrganizationSchemes
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetRegistrationSchemesAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class MdmGetRegistrationSchemesDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform,
    private val mdmClient: MdmClient
) : AbstractSingleRestDelegate<MdmGetRegistrationSchemesDelegate.Parameters, List<RegistrationScheme>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        const val PARAMETER_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val criteriaCategories = parameterContainer.getString(PARAMETER_LOCATION)
            .orForwardFail { fail -> return fail }
            .let {
                Location.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }
        return Parameters(criteriaCategories).asSuccess()
    }

    override suspend fun execute(
        parameters: Parameters,
        context: CamundaGlobalContext,
        executionInterceptor: ExecutionInterceptor
    ): Result<List<RegistrationScheme>, Fail.Incident> {
        val registrationSchemes = when (parameters.location) {
            Location.BID -> {
                val bids = context.tryGetBids().orForwardFail { return it }
                getCountriesIdFromBid(bids)
            }
            Location.SUBMISSION -> {
                val submissions = context.tryGetSubmissions().orForwardFail { return it }
                getCountriesIdFromSubmission(submissions)
            }
            Location.AWARD -> getCountriesIdFromAwards(context.awards)
        }

        return mdmClient.getOrganizationSchemes(params = registrationSchemes)
            .orForwardFail { error -> return error }
            .let { result -> handleResult(result, executionInterceptor) }
            .asSuccess()
    }


    override fun updateGlobalContext(context: CamundaGlobalContext, result: List<RegistrationScheme>): MaybeFail<Fail.Incident> {
        if (result.isEmpty())
            return MaybeFail.fail(Fail.Incident.Response.Empty(ExternalServiceName.MDM, "getOrganizationSchemes"))

        val mdm = context.processMasterData?.mdm
            ?.copy(registrationSchemes = RegistrationSchemes(result))
            ?: Mdm(registrationSchemes = RegistrationSchemes(result))

        context.processMasterData = context.processMasterData
            ?.copy(mdm = mdm)
            ?: ProcessMasterData(mdm)

        return MaybeFail.none()
    }

    private fun getCountriesIdFromBid(bids: Bids): GetRegistrationSchemesAction.Params =
        bids.details
            .flatMap { it.tenderers }
            .mapNotNull { it.address?.addressDetails?.country?.id }
            .let { countryIds -> GetRegistrationSchemesAction.Params(countryId = countryIds) }

    private fun getCountriesIdFromSubmission(submissions: Submissions): GetRegistrationSchemesAction.Params =
        submissions.details
            .flatMap { it.candidates }
            .mapNotNull { it.address?.addressDetails?.country?.id }
            .let { countryIds -> GetRegistrationSchemesAction.Params(countryId = countryIds) }

    private fun getCountriesIdFromAwards(awards: Awards): GetRegistrationSchemesAction.Params =
        awards
            .flatMap { it.suppliers }
            .mapNotNull { it.address?.addressDetails?.country?.id }
            .let { countryIds -> GetRegistrationSchemesAction.Params(countryId = countryIds) }

    private fun handleResult(
        result: GetOrganizationSchemes.Result,
        executionInterceptor: ExecutionInterceptor
    ): List<RegistrationScheme> = when (result) {
        is GetOrganizationSchemes.Result.Success -> result.registrationSchemes
            .map { RegistrationScheme(country = it.country, schemes = Schemes(it.schemes)) }

        is GetOrganizationSchemes.Result.Fail.AnotherError -> {
            val errors = result.details.errors.convertErrors()
            executionInterceptor.throwError(errors = errors)
        }
    }

    private fun List<GetRegistrationSchemesAction.Response.Error.Details>.convertErrors(): List<Errors.Error> =
        this.map { error ->
            Errors.Error(code = error.code, description = error.description)
        }

    data class Parameters(val location: Location)

    enum class Location(override val key: String) : EnumElementProvider.Key {
        AWARD("award"),
        BID("bid"),
        SUBMISSION("submission");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }

}

