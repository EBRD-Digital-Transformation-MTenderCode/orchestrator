package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
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
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CreateInvitationsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionCreateInvitationsDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateInvitationsAction.Result>(
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
    ): Result<Reply<CreateInvitationsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val additionalProcess = processInfo.additionalProcess!!

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return submissionClient.createInvitations(
            id = commandId,
            params = CreateInvitationsAction.Params(
                cpid = processInfo.cpid,
                additionalCpid = additionalProcess.cpid,
                additionalOcid = additionalProcess.ocid,
                tender = CreateInvitationsAction.Params.Tender(
                    lots = tender.lots.map { lot ->
                        CreateInvitationsAction.Params.Tender.Lot(id = lot.id)
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateInvitationsAction.Result>
    ): MaybeFail<Fail.Incident> {

        result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.CreateInvitations
                )
            )

        return MaybeFail.none()
    }

}
