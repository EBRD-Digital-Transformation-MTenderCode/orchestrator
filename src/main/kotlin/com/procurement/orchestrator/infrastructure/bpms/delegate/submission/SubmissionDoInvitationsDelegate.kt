package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.invitation.Invitation
import com.procurement.orchestrator.domain.model.invitation.Invitations
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.organization.OrganizationReferences
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.DoInvitationsAction
import org.springframework.stereotype.Component

@Component
class SubmissionDoInvitationsDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, DoInvitationsAction.Result>(
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
    ): Result<Reply<DoInvitationsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val qualifications = context.qualifications
        val submissions = context.submissions

        return submissionClient.doInvitations(
            id = commandId,
            params = DoInvitationsAction.Params(
                cpid = processInfo.cpid,
                date = requestInfo.timestamp,
                qualifications = qualifications.map { qualification ->
                    DoInvitationsAction.Params.Qualification(
                        id = qualification.id,
                        relatedSubmission = qualification.relatedSubmission,
                        statusDetails = qualification.statusDetails
                    )
                },
                submissions = submissions?.details
                    ?.map { details ->
                        DoInvitationsAction.Params.Submissions.Detail(
                            id = details.id,
                            candidates = details.candidates.map { candidate ->
                                DoInvitationsAction.Params.Submissions.Detail.Candidate(
                                    id = candidate.id,
                                    name = candidate.name
                                )
                            }
                        )
                    }?.let { DoInvitationsAction.Params.Submissions(it) }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<DoInvitationsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val invitations = data.invitations
            .map { invitation ->
                Invitation(
                    id = invitation.id,
                    status = invitation.status,
                    date = invitation.date,
                    relatedQualification = invitation.relatedQualification,
                    tenderers = OrganizationReferences(
                        invitation.tenderers
                            .map { tenderer ->
                                OrganizationReference(
                                    id = tenderer.id,
                                    name = tenderer.name
                                )
                            }
                    )
                )
            }
        context.invitations = Invitations(invitations)

        return MaybeFail.none()
    }
}
