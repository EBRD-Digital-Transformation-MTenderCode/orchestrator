package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getLotsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateUnsuccessfulAwardsAction
import org.springframework.stereotype.Component

@Component
class EvaluationCreateUnsuccessfulAwardsDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateUnsuccessfulAwardsAction.Result>(
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
    ): Result<Reply<CreateUnsuccessfulAwardsAction.Result>, Fail.Incident> {

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val lots = tender.getLotsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val owner: Owner = tender.owner
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "owner", path = "tender"))

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return evaluationClient.createUnsuccessfulAwards(
            id = commandId,
            params = CreateUnsuccessfulAwardsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                date = requestInfo.timestamp,
                lotIds = lots.map { it.id as LotId.Permanent },
                owner = owner
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: CreateUnsuccessfulAwardsAction.Result
    ): MaybeFail<Fail.Incident> {

        val awards = context.awards

        val availableAwardsById = awards.associateBy { it.id }
        val receivedAwardByIds = data.associateBy { it.id }
        val receivedAwardIds = receivedAwardByIds.keys
        val knownAwardIds = availableAwardsById.keys

        val updatedAwards = awards
            .map { award ->
                receivedAwardByIds[award.id]
                    ?.let { receivedUnsuccessfulAward ->
                        award.copy(
                            date = receivedUnsuccessfulAward.date,
                            title = receivedUnsuccessfulAward.title,
                            description = receivedUnsuccessfulAward.description,
                            status = receivedUnsuccessfulAward.status,
                            statusDetails = receivedUnsuccessfulAward.statusDetails,
                            relatedLots = receivedUnsuccessfulAward.relatedLots
                        )
                    }
                    ?: award
            }
        val newAwards = getNewElements(received = receivedAwardIds, known = knownAwardIds)
            .map { awardId ->
                receivedAwardByIds.getValue(awardId)
                    .let { unsuccessfulAward ->
                        Award(
                            id = awardId,
                            date = unsuccessfulAward.date,
                            title = unsuccessfulAward.title,
                            description = unsuccessfulAward.description,
                            status = unsuccessfulAward.status,
                            statusDetails = unsuccessfulAward.statusDetails,
                            relatedLots = unsuccessfulAward.relatedLots
                        )
                    }

            }

        context.awards = Awards(values = updatedAwards + newAwards)

        updateOutcomes(context = context, data = data)

        return MaybeFail.none()
    }

    private fun updateOutcomes(
        context: CamundaGlobalContext,
        data: CreateUnsuccessfulAwardsAction.Result
    ) {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = data
            .map { unsuccessfulAward ->
                Outcomes.Details.Award(id = unsuccessfulAward.id)
            }

        val updatedDetails = details.copy(
            awards = details.awards + newOutcomes
        )
        outcomes[platformId] = updatedDetails
        context.outcomes = outcomes
    }
}
