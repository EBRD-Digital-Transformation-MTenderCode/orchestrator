package com.procurement.orchestrator.infrastructure.bpms.delegate.clarification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ClarificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.clarification.ClarificationCommands
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.CreateEnquiryPeriodAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ClarificationCreateEnquiryPeriodDelegate(
    logger: Logger,
    private val clarificationClient: ClarificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateEnquiryPeriodAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> {
        return Unit.asSuccess()
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateEnquiryPeriodAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        return clarificationClient.createEnquiryPeriod(
            id = commandId,
            params = CreateEnquiryPeriodAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                operationType = processInfo.operationType,
                pmd = processInfo.pmd,
                country = requestInfo.country,
                owner = requestInfo.owner,
                tender = CreateEnquiryPeriodAction.Params.Tender(
                    CreateEnquiryPeriodAction.Params.Tender.TenderPeriod(
                        startDate = tender.tenderPeriod?.startDate,
                        endDate = tender.tenderPeriod?.endDate
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateEnquiryPeriodAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.CLARIFICATION,
                    action = ClarificationCommands.CreateEnquiryPeriod
                )
            )

        val receivedEnquiryPeriod = data.tender.enquiryPeriod
            .let { period ->
                Period(
                    startDate = period.startDate,
                    endDate = period.endDate
                )
            }

        context.tender = context.tender?.copy(enquiryPeriod = receivedEnquiryPeriod)
            ?: Tender(enquiryPeriod = receivedEnquiryPeriod)

        return MaybeFail.none()
    }
}
