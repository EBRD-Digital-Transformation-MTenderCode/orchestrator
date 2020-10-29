package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.contract.RelatedProcess
import com.procurement.orchestrator.domain.model.contract.RelatedProcessTypes
import com.procurement.orchestrator.domain.model.contract.RelatedProcesses
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.OutsourcingPnAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessOutsourcingPNDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, OutsourcingPnAction.Result>(
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
    ): Result<Reply<OutsourcingPnAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.outsourcingPn(
            id = commandId,
            params = OutsourcingPnAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                cpidFa = processInfo.relatedProcess?.cpid
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<OutsourcingPnAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.OutsourcingPn
                )
            )

        val receivedRelatedProcesses = data.relatedProcesses.map { it.convert() }
        val updatedRelatedProcesses = context.relatedProcesses updateBy RelatedProcesses(receivedRelatedProcesses)

        context.relatedProcesses = updatedRelatedProcesses

        return MaybeFail.none()
    }
}

private fun OutsourcingPnAction.Result.RelatedProcess.convert() =
    RelatedProcess(
        id = id,
        identifier = identifier,
        relationship = RelatedProcessTypes(relationship),
        scheme = scheme,
        uri = uri
    )
