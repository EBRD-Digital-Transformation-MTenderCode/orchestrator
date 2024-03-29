package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluation

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.EvaluationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.EvaluationCommands
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.StartAwardPeriodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class EvaluationStartAwardPeriodDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, StartAwardPeriodAction.Result>(
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
    ): Result<Reply<StartAwardPeriodAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        return evaluationClient.startAwardPeriod(
            id = commandId,
            params = StartAwardPeriodAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                date = requestInfo.timestamp
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<StartAwardPeriodAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.EVALUATION,
                    action = EvaluationCommands.StartAwardPeriod
                )
            )

        val awardPeriod = Period(startDate = data.tender.awardPeriod.startDate)
        val updatedTender = context.tender?.copy(awardPeriod = awardPeriod)
            ?: Tender(awardPeriod = awardPeriod)
        context.tender = updatedTender

        return MaybeFail.none()
    }
}
