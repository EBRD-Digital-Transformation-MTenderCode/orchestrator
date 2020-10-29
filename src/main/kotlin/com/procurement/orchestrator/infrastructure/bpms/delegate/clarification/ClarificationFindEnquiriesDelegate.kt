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
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.enquiry.Enquiries
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.FindEnquiriesAction
import com.procurement.orchestrator.infrastructure.client.web.clarification.action.convertToGlobalContextEntity
import org.springframework.stereotype.Component

@Component
class ClarificationFindEnquiriesDelegate(
    logger: Logger,
    private val clarificationClient: ClarificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<ClarificationFindEnquiriesDelegate.Parameters, FindEnquiriesAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val NAME_PARAMETER_OF_IS_ANSWER = "isAnswer"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val isAnswer = parameterContainer.getBooleanOrNull(name = NAME_PARAMETER_OF_IS_ANSWER)
            .orForwardFail { fail -> return fail }

        return success(Parameters(isAnswer = isAnswer))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindEnquiriesAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return clarificationClient.findEnquiries(
            id = commandId,
            params = FindEnquiriesAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                isAnswer = parameters.isAnswer
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindEnquiriesAction.Result>
    ): MaybeFail<Fail.Incident> {

        val responseData = result.orNull
            ?: return MaybeFail.none()

        val tender = context.tender ?: Tender()

        val receivedEnquiriesById = responseData.map { it.convertToGlobalContextEntity() }
            .associateBy { it.id }

        val dbEnquiries = tender.enquiries

        val updatedDbEnquiries = dbEnquiries.map { enquiry ->
            receivedEnquiriesById[enquiry.id]
                ?.let { received -> enquiry.updateBy(received) }
                ?: enquiry
        }

        val dbEnquiriesIds = dbEnquiries.map { it.id }
        val receivedEnquiriesIds = receivedEnquiriesById.keys

        val newEnquiries = getNewElements(known = dbEnquiriesIds, received = receivedEnquiriesIds)
            .map { id -> receivedEnquiriesById.getValue(id) }

        val updatedTender = tender.copy(enquiries = Enquiries(updatedDbEnquiries + newEnquiries))

        context.tender = updatedTender

        return MaybeFail.none()
    }

    class Parameters(val isAnswer: Boolean?)
}
