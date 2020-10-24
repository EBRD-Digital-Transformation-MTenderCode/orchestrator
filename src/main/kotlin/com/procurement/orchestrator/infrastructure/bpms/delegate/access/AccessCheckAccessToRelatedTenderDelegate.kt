package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import org.springframework.stereotype.Component

@Component
class AccessCheckAccessToRelatedTenderDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
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
        val relatedProcess = context.processInfo.relatedProcess!!
        val cpid: Cpid = relatedProcess.cpid
        val ocid: Ocid = relatedProcess.ocid
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "relatedProcess.ocid", path = "processInfo"))

        val requestInfo = context.requestInfo
        val token: Token = requestInfo.token
            ?: return failure(Fail.Incident.Bpms.Context.Missing(name = "token", path = "requestInfo"))
        val owner: Owner = requestInfo.owner

        return accessClient.checkAccessToTender(
            id = commandId,
            params = CheckAccessToTenderAction.Params(
                cpid = cpid, ocid = ocid, token = token, owner = owner
            )
        )
    }
}
