package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getDetailsIfNotEmpty
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionStateByIdsAction
import org.springframework.stereotype.Component

@Component
class DossierGetSubmissionStateByIdsDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetSubmissionStateByIdsAction.Result>(
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
    ): Result<Reply<GetSubmissionStateByIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val submissions = context.submissions.getDetailsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        return dossierClient.getSubmissionStateByIds(
            id = commandId,
            params = GetSubmissionStateByIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                submissionIds = submissions.map { it.id }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: GetSubmissionStateByIdsAction.Result
    ): MaybeFail<Fail.Incident> {

        val submissions = context.submissions
            .getDetailsIfNotEmpty()
            .orReturnFail { fail -> return MaybeFail.fail(fail) }

        val receivedSubmissionsByIds = data.associateBy { it.id }

        val updatedSubmission = submissions
            .map { submission ->
                receivedSubmissionsByIds[submission.id]
                    ?.let {
                        submission.copy(status = it.status)
                    }
                    ?: submission
            }

        context.submissions = Submissions(details = Details(values = updatedSubmission))

        return MaybeFail.none()
    }
}
