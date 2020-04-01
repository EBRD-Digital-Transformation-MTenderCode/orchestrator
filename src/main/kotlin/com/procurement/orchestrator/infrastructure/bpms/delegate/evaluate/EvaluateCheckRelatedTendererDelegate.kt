package com.procurement.orchestrator.infrastructure.bpms.delegate.evaluate

import com.procurement.orchestrator.application.client.EvaluateClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import org.springframework.stereotype.Component

@Component
class EvaluateCheckRelatedTendererDelegate(
    logger: Logger,
    private val evaluateClient: EvaluateClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val award = context.awards.getAwardIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        val requirementResponse = award.requirementResponses
            .getRequirementResponseIfOnlyOne()
            .doOnError { return failure(it) }
            .get

        val relatedTendererId = requirementResponse.relatedTenderer?.id
        val requirementId = requirementResponse.requirement?.id

        val processInfo = context.processInfo
        return evaluateClient.checkRelatedTenderer(
            params = CheckRelatedTendererAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                awardId = award.id,
                relatedTendererId = relatedTendererId,
                requirementId = requirementId
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
