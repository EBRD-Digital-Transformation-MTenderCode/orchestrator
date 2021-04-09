package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
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
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckExistenceOfInvitationAction
import org.springframework.stereotype.Component

@Component
class SubmissionCheckExistenceOfInvitationDelegate(
    logger: Logger,
    private val client: SubmissionClient,
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

        val processInfo = context.processInfo
        val bids = context.tryGetBids()
            .orForwardFail { fail -> return fail }

        return client.checkExistenceOfInvitation(
            id = commandId,
            params = CheckExistenceOfInvitationAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                bids = CheckExistenceOfInvitationAction.Params.Bids(
                    details = bids.details.map { detail ->
                        CheckExistenceOfInvitationAction.Params.Bids.Detail(
                            id = detail.id,
                            tenderers = detail.tenderers.map { tenderer ->
                                CheckExistenceOfInvitationAction.Params.Bids.Detail.Tenderer(id = tenderer.id)
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
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
