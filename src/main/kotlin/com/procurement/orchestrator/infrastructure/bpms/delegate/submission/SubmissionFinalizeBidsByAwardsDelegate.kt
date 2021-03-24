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
import com.procurement.orchestrator.infrastructure.client.web.submission.SubmissionCommands
import com.procurement.orchestrator.infrastructure.client.web.submission.action.FinalizeBidsByAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.submission.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class SubmissionFinalizeBidsByAwardsDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FinalizeBidsByAwardsAction.Result>(
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
    ): Result<Reply<FinalizeBidsByAwardsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val awards = context.awards

        return submissionClient.finalizeBidsByAwards(
            id = commandId,
            params = FinalizeBidsByAwardsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                awards = awards.map { award ->
                    FinalizeBidsByAwardsAction.Params.Award(
                        id = award.id,
                        status = award.status,
                        statusDetails = award.statusDetails,
                        relatedBid = award.relatedBid
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FinalizeBidsByAwardsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.SUBMISSION,
                    action = SubmissionCommands.FinalizeBidsByAwards
                )
            )

        val receivedBids = data.bids.details
            .map { it.toDomain() }
            .let { Bids(details = BidsDetails(it)) }

        context.bids = receivedBids

        return MaybeFail.none()
    }
}
