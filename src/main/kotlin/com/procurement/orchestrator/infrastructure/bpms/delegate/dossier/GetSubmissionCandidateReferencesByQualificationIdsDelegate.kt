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
import com.procurement.orchestrator.domain.model.candidate.Candidates
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.submission.Details
import com.procurement.orchestrator.domain.model.submission.Submission
import com.procurement.orchestrator.domain.model.submission.Submissions
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.dossier.DossierCommands
import com.procurement.orchestrator.infrastructure.client.web.dossier.action.GetSubmissionCandidateReferencesByQualificationIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class GetSubmissionCandidateReferencesByQualificationIdsDelegate(
    logger: Logger,
    private val dossierClient: DossierClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetSubmissionCandidateReferencesByQualificationIdsAction.Result>(
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
    ): Result<Reply<GetSubmissionCandidateReferencesByQualificationIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val qualifications = context.getQualificationsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        return dossierClient.getSubmissionCandidateReferencesByQualificationIds(
            id = commandId,
            params = GetSubmissionCandidateReferencesByQualificationIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                qualifications = qualifications.map {
                    GetSubmissionCandidateReferencesByQualificationIdsAction.Params.Qualification(
                        id = it.id,
                        relatedSubmission = it.relatedSubmission
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetSubmissionCandidateReferencesByQualificationIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.DOSSIER,
                    action = DossierCommands.GetSubmissionCandidateReferencesByQualificationIds
                )
            )

        val contextSubmissions = context.submissions?.details ?: Submissions().details

        val newSubmissions = data.submissions
            .details
            .map { detail ->
                Submission(
                    id = detail.id,
                    candidates = Candidates(
                        values = detail.candidates
                            .map { candidate ->
                                Organization(id = candidate.id, name = candidate.name)
                            }
                    )
                )
            }

        context.submissions = Submissions(details = Details(values = contextSubmissions + newSubmissions))
        return MaybeFail.none()
    }
}