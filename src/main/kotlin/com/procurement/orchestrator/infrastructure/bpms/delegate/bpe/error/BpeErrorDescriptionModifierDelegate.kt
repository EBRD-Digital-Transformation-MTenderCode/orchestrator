package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.error

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.mdm.MdmCommands
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
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
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> {

        val errors = context.errors
            ?: return success(Reply.None)
        val requestInfo = context.requestInfo
        val params = GetErrorDescriptionsAction.Params(
            codes = errors.map { it.code },
            language = requestInfo.language
        )
        return mdmClient.getErrorDescription(id = commandId, params = params)
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<GetErrorDescriptionsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.MDM, action = MdmCommands.GetErrorDescriptions)
            )

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
