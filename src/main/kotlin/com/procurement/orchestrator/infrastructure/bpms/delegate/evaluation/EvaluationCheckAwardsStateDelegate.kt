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
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAwardsStateAction
import org.springframework.stereotype.Component

@Component
class EvaluationCheckAwardsStateDelegate(
    logger: Logger,
    private val evaluationClient: EvaluationClient,
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
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val cpid: Cpid = processInfo.cpid!!
        val ocid: Ocid = processInfo.ocid!!

        val tender = context.tryGetTender()
            .orForwardFail { return it }

        return evaluationClient.checkAwardsState(
            id = commandId,
            params = CheckAwardsStateAction.Params(
                cpid = cpid,
                ocid = ocid,
                pmd = processInfo.pmd,
                operationType = processInfo.operationType,
                country = requestInfo.country,
                awards = context.awards.map { award ->
                    CheckAwardsStateAction.Params.Award(award.id)
                },
                tender = CheckAwardsStateAction.Params.Tender(
                    tender.lots.map { lot ->
                        CheckAwardsStateAction.Params.Tender.Lot(lot.id)
                    })

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
