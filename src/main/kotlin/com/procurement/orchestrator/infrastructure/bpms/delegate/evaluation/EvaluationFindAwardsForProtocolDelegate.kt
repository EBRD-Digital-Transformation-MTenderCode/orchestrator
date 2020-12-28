package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.award.Awards
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.FindAwardsForProtocolAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.toDomain
import org.springframework.stereotype.Component

@Component
class EvaluationFindAwardsForProtocolDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindAwardsForProtocolAction.Result>(
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
    ): Result<Reply<FindAwardsForProtocolAction.Result>, Fail.Incident> {

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        return evaluationClient.findAwardsForProtocol(
            id = commandId,
            params = FindAwardsForProtocolAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = FindAwardsForProtocolAction.Params.Tender(
                    tender.lots.map { lot ->
                        FindAwardsForProtocolAction.Params.Tender.Lot(lot.id)
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindAwardsForProtocolAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val awards = data.awards.map { award -> award.toDomain() }

        context.awards = Awards(awards)

        return MaybeFail.none()
    }
}
