package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckRelationAction
import org.springframework.stereotype.Component

@Component
class AccessCheckRelationWithAdditionalProcessDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessCheckRelationWithAdditionalProcessDelegate.Parameters, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        private const val EXISTENCE_RELATION = "existenceRelation"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val relationExists = parameterContainer.getBooleanOrNull(EXISTENCE_RELATION)
            .orForwardFail { fail -> return fail }

        return Result.success(Parameters(relationExists))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<Unit>, Fail.Incident> {
        val processInfo = context.processInfo
        val relatedProcess = processInfo.relatedProcess!!
        val additionalProcess = processInfo.additionalProcess!!

        return accessClient.checkRelationDelegate(
            id = commandId,
            params = CheckRelationAction.Params(
                cpid = relatedProcess.cpid,
                ocid = relatedProcess.ocid!!,
                relatedCpid = additionalProcess.cpid,
                operationType = processInfo.operationType,
                existenceRelation = parameters.relationExists
            )
        )
    }

    class Parameters(val relationExists: Boolean?)
}
