package com.procurement.orchestrator.infrastructure.bpms.delegate.clarification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ClarificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.enquiry.Enquiries
import com.procurement.orchestrator.domain.model.enquiry.Enquiry
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiryIdsAction
import org.springframework.stereotype.Component

@Component
class ClarificationFindEnquiryIdsDelegate(
    logger: Logger,
    private val clarificationClient: ClarificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<ClarificationFindEnquiryIdsDelegate.Parameters, FindEnquiryIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val NAME_PARAMETER_OF_IS_ANSWER = "isAnswer"
    }

    override fun parameters(parameterContainer: ParameterContainer)
        : Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val isAnswer = parameterContainer.getBooleanOrNull(name = NAME_PARAMETER_OF_IS_ANSWER)
            .orForwardFail { fail -> return fail }

        return Parameters(isAnswer = isAnswer)
            .asSuccess()
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindEnquiryIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return clarificationClient.findEnquiryIds(
            id = commandId,
            params = FindEnquiryIdsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                isAnswer = parameters.isAnswer
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindEnquiryIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val tender = context.tender
            ?: Tender()

        val receivedEnquiries = data
            .map { Enquiry(id = it) }

        val enquiries = tender.enquiries

        val newEnquiries = getNewElements(known = enquiries, received = receivedEnquiries)

        val updatedTender = tender.copy(enquiries = Enquiries(enquiries + newEnquiries))

        context.tender = updatedTender

        return MaybeFail.none()
    }

    class Parameters(val isAnswer: Boolean?)
}
