package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRequirementResponsesAction
import org.springframework.stereotype.Component

@Component
class AccessValidateRequirementResponsesDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessValidateRequirementResponsesDelegate.Parameters, ValidateRequirementResponsesAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let { location ->
                Location.orNull(location)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            actualValue = location,
                            expectedValues = Location.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(location = location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<ValidateRequirementResponsesAction.Result>, Fail.Incident> {

        val submissions = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }

        val submission = submissions.getDetailsIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        return accessClient.validateRequirementResponses(
            id = commandId,
            params = ValidateRequirementResponsesAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                requirementResponse = submission.requirementResponses
                    .map { requirementResponse ->
                        ValidateRequirementResponsesAction.Params.RequirementResponse(
                            id = requirementResponse.id,
                            value = requirementResponse.value,
                            requirement = requirementResponse.requirement
                                ?.let { requirement ->
                                    ValidateRequirementResponsesAction.Params.RequirementResponse.Requirement(
                                        id = requirement.id
                                    )
                                },
                            relatedCandidate = requirementResponse.relatedCandidate
                                ?.let { relatedCandidate ->
                                    ValidateRequirementResponsesAction.Params.RequirementResponse.RelatedCandidate(
                                        id = relatedCandidate.id,
                                        name = relatedCandidate.name
                                    )
                                }
                        )
                    },
                organizationIds = submission.candidates
                    .map { organization ->
                        organization.id
                    },
                operationType = processInfo.operationType
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<ValidateRequirementResponsesAction.Result>
    ): MaybeFail<Fail.Incident> {

        val submissions: Submissions = context.tryGetSubmissions()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val submission = submissions.getDetailsIfOnlyOne()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val requirementResponses = if (result.isEmpty)
            RequirementResponses()
        else
            RequirementResponses(
                result.get
                    .map { requirementResponse ->
                        RequirementResponse(
                            id = requirementResponse.id,
                            value = requirementResponse.value,
                            requirement = RequirementReference(id = requirementResponse.requirement.id),
                            relatedCandidate = requirementResponse.relatedCandidate
                                .let { relatedCandidate ->
                                    OrganizationReference(
                                        id = relatedCandidate.id,
                                        name = relatedCandidate.name
                                    )
                                }
                        )
                    }
            )

        val updatedSubmission = submission.copy(requirementResponses = requirementResponses)
        val updatedSubmissions = submissions.copy(details = Details(updatedSubmission))
        context.submissions = updatedSubmissions

        return MaybeFail.none()
    }

    class Parameters(val location: Location)

    enum class Location(@JsonValue override val key: String) : EnumElementProvider.Key {

        TENDER("submission");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Location.orThrow(name)
        }
    }
}
