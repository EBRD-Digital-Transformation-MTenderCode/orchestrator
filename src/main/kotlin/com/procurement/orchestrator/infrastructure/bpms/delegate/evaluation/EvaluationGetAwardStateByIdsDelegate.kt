package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardsIfNotEmpty
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.domain.util.extension.toSetBy
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.EvaluationCommands
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class EvaluationGetAwardStateByIdsDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetAwardStateByIdsAction.Result>(
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
    ): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident> {

        val awards = context.getAwardsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        return evaluationClient.getAwardStateByIds(
            id = commandId,
            params = GetAwardStateByIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                ids = awards.map { award -> award.id }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetAwardStateByIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.EVALUATION, action = EvaluationCommands.GetAwardStateByIds)
            )

        val awards = context.getAwardsIfNotEmpty()
            .orReturnFail { return MaybeFail.fail(it) }

        val receivedAwardByIds: Map<AwardId, GetAwardStateByIdsAction.Result.Award> = data.associateBy { it.id }
        val receivedAwardIds = receivedAwardByIds.keys
        val knowAwardIds = awards.toSetBy { it.id }

        val updatedAwards = awards
            .map { award ->
                receivedAwardByIds[award.id]
                    ?.let { receivedLot ->
                        award.copy(status = receivedLot.status, statusDetails = receivedLot.statusDetails)
                    }
                    ?: award
            }
        val newAwards = getNewElements(received = receivedAwardIds, known = knowAwardIds)
            .map { id ->
                receivedAwardByIds.getValue(id)
                    .let { award ->
                        Award(id = award.id, status = award.status, statusDetails = award.statusDetails)
                    }
            }

        context.awards = Awards(updatedAwards + newAwards)

        return MaybeFail.none()
    }
}
