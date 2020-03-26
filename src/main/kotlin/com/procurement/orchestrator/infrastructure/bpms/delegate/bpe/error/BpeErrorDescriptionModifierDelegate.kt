package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.error

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import org.springframework.stereotype.Component

@Component
class BpeErrorDescriptionModifierDelegate(
    logger: Logger,
    private val mdmClient: MdmClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetErrorDescriptionsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> {

        val errors = context.errors
            ?: return success(
                Reply(
                    id = EMPTY_REPLY_ID,
                    version = "",
                    status = Reply.Status.SUCCESS,
                    result = Reply.Result.Success(GetErrorDescriptionsAction.Result(emptyList()))
                )
            )
        val requestInfo = context.requestInfo
        val params = GetErrorDescriptionsAction.Params(
            codes = errors.map { it.code },
            language = requestInfo.language
        )
        return mdmClient.getErrorDescription(params = params)
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: GetErrorDescriptionsAction.Result
    ): MaybeFail<Fail.Incident.Bpmn> {
        val errorDescriptions = data.associateBy(
            keySelector = { it.code },
            valueTransform = { it.description }
        )
        val existingErrors = context.errors

        if (existingErrors != null) {
            val updatedErrors = existingErrors.map { error ->
                Errors.Error(
                    code = error.code,
                    description = errorDescriptions[error.code] ?: error.description
                )
            }
            context.errors = Errors(updatedErrors)
        }

        return MaybeFail.none()
    }
}
