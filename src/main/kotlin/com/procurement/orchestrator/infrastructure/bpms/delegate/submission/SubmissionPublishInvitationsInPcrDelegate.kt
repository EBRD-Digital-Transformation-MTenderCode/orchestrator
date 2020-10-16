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
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.PublishInvitationsInPcrAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionPublishInvitationsInPcrDelegate(
    logger: Logger,
    private val client: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, PublishInvitationsInPcrAction.Result>(
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
    ): Result<Reply<PublishInvitationsInPcrAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return client.publishInvitationsInPcr(
            id = commandId,
            params = PublishInvitationsInPcrAction.Params(
                cpid = processInfo.cpid
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<PublishInvitationsInPcrAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.PublishInvitationsInPcr
                )
            )

        val invitations = data.invitations.map { convertToGlobalContextEntity(it) }
        context.invitations = Invitations(invitations)

        return MaybeFail.none()
    }

    private fun convertToGlobalContextEntity(invitation: PublishInvitationsInPcrAction.Result.Invitation) =
        Invitation(
            id = invitation.id,
            date = invitation.date,
            status = invitation.status,
            relatedQualification = invitation.relatedQualification,
            tenderers = invitation.tenderers.map { tenderer ->
                OrganizationReference(
                    id = tenderer.id,
                    name = tenderer.name
                )
            }.let { OrganizationReferences(it) }
        )
}
