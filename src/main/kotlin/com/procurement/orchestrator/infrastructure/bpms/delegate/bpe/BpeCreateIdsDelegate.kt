package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asOption
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.SubmissionId
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeCreateIdsDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<Parameters, Response>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let {
                Location.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }
        return Parameters(location = location)
            .asSuccess()
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Option<Response>, Fail.Incident> {

        val response = when (parameters.location) {
            Location.AWARD_REQUIREMENT_RESPONSE -> {
                Response()
            }
            Location.TENDER_AMENDMENT -> {
                Response()
            }
            Location.SUBMISSION_DETAILS -> {

                val submissions = context.submissions
                    .getDetailsIfNotEmpty()
                    .orForwardFail { fail -> return fail }

                val submissionsIds = submissions.asSequence()
                    .map { submission ->
                        val temp = submission.id as SubmissionId.Temporal
                        val permanent = SubmissionId.Permanent.generate() as SubmissionId.Permanent
                        temp to permanent
                    }
                    .toMap()

                Response(submissionsIds = submissionsIds)
            }
        }

        return response.asOption()
            .asSuccess()
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: Response
    ): MaybeFail<Fail.Incident.Bpmn> {

        when (parameters.location) {
            Location.AWARD_REQUIREMENT_RESPONSE -> {
            }
            Location.TENDER_AMENDMENT -> {
            }
            Location.SUBMISSION_DETAILS -> {
                val submissions = context.submissions.details

                val updatedSubmissions = Submissions(
                    details = Details(
                        values = submissions.map { submission ->
                            data.submissionsIds[submission.id as SubmissionId.Temporal]
                                ?.let { submission.copy(id = it as SubmissionId) }
                                ?: submission
                        }
                    )
                )
                context.submissions = updatedSubmissions
            }
        }

        return MaybeFail.none()
    }
}

class Response(
    val requirementResponsesIds: Map<RequirementResponseId.Temporal, RequirementResponseId.Permanent> = emptyMap(),
    val amendmentsIds: Map<AmendmentId.Temporal, AmendmentId.Permanent> = emptyMap(),
    val submissionsIds: Map<SubmissionId.Temporal, SubmissionId.Permanent> = emptyMap()
)

class Parameters(val location: Location)

enum class Location(override val key: String) : EnumElementProvider.Key {

    AWARD_REQUIREMENT_RESPONSE("award.requirementResponse"),
    TENDER_AMENDMENT("tender.amendment"),
    SUBMISSION_DETAILS("submission");

    override fun toString(): String = key

    companion object : EnumElementProvider<Location>(info = info())
}
