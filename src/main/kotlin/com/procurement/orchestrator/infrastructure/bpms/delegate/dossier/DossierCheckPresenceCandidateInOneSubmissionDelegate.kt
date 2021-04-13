package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetSubmissions
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPresenceCandidateInOneSubmissionAction
import org.springframework.stereotype.Component

@Component
class DossierCheckPresenceCandidateInOneSubmissionDelegate(
    logger: Logger,
    private val client: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {
        val details = context.tryGetSubmissions()
            .orForwardFail { fail -> return fail }
            .getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }


        return client.checkPresenceCandidateInOneSubmission(
            id = commandId,
            params = CheckPresenceCandidateInOneSubmissionAction.Params(
                submissions = CheckPresenceCandidateInOneSubmissionAction.Params.Submissions(
                    details = details.map { detail ->
                        CheckPresenceCandidateInOneSubmissionAction.Params.Submissions.Detail(
                            id = detail.id,
                            candidates = detail.candidates.map { candidate ->
                                CheckPresenceCandidateInOneSubmissionAction.Params.Submissions.Detail.Candidate(candidate.id)
                            }
                        )
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident> = MaybeFail.none()
}
