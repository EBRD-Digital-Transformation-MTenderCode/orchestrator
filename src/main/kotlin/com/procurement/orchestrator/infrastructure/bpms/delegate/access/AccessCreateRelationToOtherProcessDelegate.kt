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
import com.procurement.orchestrator.domain.model.contract.RelatedProcess
import com.procurement.orchestrator.domain.model.contract.RelatedProcessTypes
import com.procurement.orchestrator.domain.model.contract.RelatedProcesses
import com.procurement.orchestrator.domain.model.tender.ProcedureOutsourcing
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateRelationToOtherProcessAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessCreateRelationToOtherProcessDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateRelationToOtherProcessAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Result.success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<CreateRelationToOtherProcessAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return accessClient.createRelationToOtherProcess(
            id = commandId,
            params = CreateRelationToOtherProcessAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                relatedCpid = processInfo.relatedProcess?.cpid,
                relatedOcid = processInfo.relatedProcess?.ocid,
                operationType = processInfo.operationType
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateRelationToOtherProcessAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.CreateRelationToOtherProcess
                )
            )

        val receivedRelatedProcesses = data.relatedProcesses.map { it.convert() }
        val updatedRelatedProcesses = context.relatedProcesses updateBy RelatedProcesses(receivedRelatedProcesses)

        context.relatedProcesses = updatedRelatedProcesses

        if (data.tender != null) {
            val tender = context.tender ?: Tender()
            val updatedTender = tender.copy(
                procedureOutsourcing = tender.procedureOutsourcing
                    ?.copy(
                        procedureOutsourced = data.tender.procedureOutsourcing.procedureOutsourced
                    )
                    ?: ProcedureOutsourcing(
                        procedureOutsourced = data.tender.procedureOutsourcing.procedureOutsourced
                    )
            )
            context.tender = updatedTender
        }

        return MaybeFail.none()
    }
}

private fun CreateRelationToOtherProcessAction.Result.RelatedProcess.convert() =
    RelatedProcess(
        id = id,
        identifier = identifier,
        relationship = RelatedProcessTypes(relationship),
        scheme = scheme,
        uri = uri
    )
