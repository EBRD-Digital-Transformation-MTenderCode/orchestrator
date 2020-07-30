package com.procurement.orchestrator.infrastructure.bpms.delegate.dossier

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.DossierClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getQualificationsIfNotEmpty
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.DossierCommands
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.FinalizeSubmissionsAction
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.convertToGlobalContextEntity
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class DossierFinalizeSubmissionsDelegate(
    logger: Logger,
    private val client: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FinalizeSubmissionsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<FinalizeSubmissionsAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo
        val qualifications = context.getQualificationsIfNotEmpty()
            .orForwardFail { error -> return error }

        return client.finalizeSubmissions(
            id = commandId,
            params = FinalizeSubmissionsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                qualifications = qualifications.map {
                    FinalizeSubmissionsAction.Params.Qualification(
                        id = it.id,
                        status = it.status,
                        relatedSubmission = it.relatedSubmission
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FinalizeSubmissionsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.DOSSIER,
                    action = DossierCommands.FinalizeSubmissions
                )
            )

        val receivedSubmissions = Details(data.submissions.details.map { it.convertToGlobalContextEntity() })

        val submissionsFromDb = context.submissions

        val updatedSubmissions =
            if (submissionsFromDb == null)
                Details(receivedSubmissions)
            else
                Details(submissionsFromDb.details updateBy receivedSubmissions)

        context.submissions = Submissions(updatedSubmissions)

        return MaybeFail.none()
    }
}
