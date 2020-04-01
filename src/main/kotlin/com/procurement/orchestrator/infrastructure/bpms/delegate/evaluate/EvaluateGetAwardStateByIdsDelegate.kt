package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluate

import com.procurement.orchestrator.application.client.EvaluateClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.domain.util.extension.toSetBy
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import org.springframework.stereotype.Component

@Component
class EvaluateGetAwardStateByIdsDelegate(
    logger: Logger,
    private val evaluateClient: EvaluateClient,
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
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetAwardStateByIdsAction.Result>, Fail.Incident> {

        val awards = context.awards
            .takeIf { it.isNotEmpty() }
            ?: return failure(Fail.Incident.Bpe(description = "The global context does not contain a 'Awards' object."))

        val processInfo = context.processInfo
        return evaluateClient.getAwardStateByIds(
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
        data: GetAwardStateByIdsAction.Result
    ): MaybeFail<Fail.Incident> {
        val awards = context.awards
            .takeIf { it.isNotEmpty() }
            ?: return MaybeFail.fail(
                Fail.Incident.Bpe(description = "The global context does not contain a 'Awards' object.")
            )

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

        context.awards = updatedAwards + newAwards

        return MaybeFail.none()
    }
}
