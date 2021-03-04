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
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FindDocumentsByBidIdAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.fromDomain
import com.procurement.orchestrator.infrastructure.client.web.submission.action.toDomain
import org.springframework.stereotype.Component

@Component
class SubmissionFindDocumentsByBidIdDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindDocumentsByBidIdAction.Result>(
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
    ): Result<Reply<FindDocumentsByBidIdAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return submissionClient.findDocumentsByBidIds(
            id = commandId,
            params = FindDocumentsByBidIdAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                bids = FindDocumentsByBidIdAction.Params.Bids.fromDomain(context.bids)
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindDocumentsByBidIdAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val receivedBids = data.bids.details
            .map { detail -> detail.toDomain() }
            .let { BidsDetails(it) }

        context.bids = Bids(details = receivedBids)

        return MaybeFail.none()
    }
}
