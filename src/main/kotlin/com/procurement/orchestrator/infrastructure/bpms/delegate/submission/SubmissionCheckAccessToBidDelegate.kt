package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.CheckAccessToBidAction
import org.springframework.stereotype.Component

@Component
class SubmissionCheckAccessToBidDelegate(
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
        val requestInfo = context.requestInfo
        val bids = context.tryGetBids().orForwardFail { return it }

        return client.checkAccessToBid(
            id = commandId,
            params = CheckAccessToBidAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                token = requestInfo.token,
                owner = requestInfo.owner,
                bids = bids.toParams()
            )
        )
    }

    private fun Bids.toParams() =
        CheckAccessToBidAction.Params.Bids(details = this.details.map { it.toParams() })

    private fun Bid.toParams() =
        CheckAccessToBidAction.Params.Bids.Detail(id = this.id)

}
