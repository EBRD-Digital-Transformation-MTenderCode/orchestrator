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
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.SetStateForBidsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionSetStateForBidsDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, SetStateForBidsAction.Result>(
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
    ): Result<Reply<SetStateForBidsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val bids = context.tryGetBids().orForwardFail { return it }

        return submissionClient.setStateForBids(
            id = commandId,
            params = SetStateForBidsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                pmd = processInfo.pmd,
                operationType = processInfo.operationType,
                country = requestInfo.country,
                bids = bids.toParams()
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<SetStateForBidsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.SetTenderPeriod
                )
            )

        val receivedBids = data.bids.details
            .map { it.toDomain() }
            .let { Bids(details = BidsDetails(it)) }

        val bids = context.tryGetBids().orReturnFail { return MaybeFail.fail(it) }
        val updatedBids = bids.updateBy(receivedBids)

        context.bids = updatedBids

        return MaybeFail.none()
    }

    private fun Bids.toParams() =
        SetStateForBidsAction.Params.Bids(details = this.details.map { it.toParams() })

    private fun Bid.toParams() =
        SetStateForBidsAction.Params.Bids.Detail(id = this.id)

}
