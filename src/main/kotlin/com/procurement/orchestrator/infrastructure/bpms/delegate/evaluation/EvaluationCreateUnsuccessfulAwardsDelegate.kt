package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getLotsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.EvaluationCommands
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

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo
        return evaluationClient.createUnsuccessfulAwards(
            id = commandId,
            params = CreateUnsuccessfulAwardsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                date = requestInfo.timestamp,
                lotIds = lots.map { it.id as LotId.Permanent },
                operationType = processInfo.operationType
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateUnsuccessfulAwardsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = "eEvaluation",
                    action = EvaluationCommands.CreateUnsuccessfulAwards
                )
            )

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
                            relatedLots = RelatedLots(receivedUnsuccessfulAward.relatedLots)
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
                            relatedLots = RelatedLots(unsuccessfulAward.relatedLots)
                        )
                    }

            }

        context.awards = Awards(values = updatedAwards + newAwards)

        return MaybeFail.none()
    }

}
