package com.procurement.orchestrator.infrastructure.bpms.delegate.submission

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.SubmissionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.submission.action.ValidateTenderPeriodAction
import org.springframework.stereotype.Component

@Component
class SubmissionValidateTenderPeriodDelegate(
    logger: Logger,
    private val submissionClient: SubmissionClient,
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

        return submissionClient.validateTenderPeriod(
            id = commandId,
            params = ValidateTenderPeriodAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                date = requestInfo.timestamp,
                country = requestInfo.country,
                pmd = processInfo.pmd,
                operationType = processInfo.operationType,
                tender = ValidateTenderPeriodAction.Params.Tender(
                    tenderPeriod = ValidateTenderPeriodAction.Params.Tender.TenderPeriod(
                        endDate = context.tender?.tenderPeriod?.endDate
                    )
                )

            )
        )
    }
}
