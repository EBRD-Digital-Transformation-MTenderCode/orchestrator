package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.SubmissionStatus
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.DossierCommands
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.SetStateForSubmissionAction
import org.springframework.stereotype.Component

@Component
class DossierSetStateForSubmissionDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<DossierSetStateForSubmissionDelegate.Parameters, SetStateForSubmissionAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val STATUS_NAME_ATTRIBUTE = "status"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val status = parameterContainer.getString(STATUS_NAME_ATTRIBUTE)
            .orForwardFail { fail -> return fail }
            .let { status ->
                SubmissionStatus.orNull(status)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = STATUS_NAME_ATTRIBUTE,
                            expectedValues = SubmissionStatus.allowedElements.keysAsStrings(),
                            actualValue = status
                        )
                    )
            }
        return Parameters(
            status = status
        )
            .asSuccess()
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<SetStateForSubmissionAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val submission = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfOnlyOne()
            .orForwardFail { fail -> return fail }

        return dossierClient.setStateForSubmission(
            id = commandId,
            params = SetStateForSubmissionAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                submission = SetStateForSubmissionAction.Params.Submission(
                    id = submission.id,
                    status = parameters.status
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<SetStateForSubmissionAction.Result>
    ): MaybeFail<Fail.Incident> {

        val submission = context.tryGetSubmissions()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }
            .getDetailsIfOnlyOne()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = "eDossier",
                    action = DossierCommands.SetStateForSubmission
                )
            )

        val updatedSubmission = submission.copy(status = data.status)

        context.submissions = Submissions(details = Details(updatedSubmission))

        return MaybeFail.none()
    }

    class Parameters(val status: SubmissionStatus)
}
