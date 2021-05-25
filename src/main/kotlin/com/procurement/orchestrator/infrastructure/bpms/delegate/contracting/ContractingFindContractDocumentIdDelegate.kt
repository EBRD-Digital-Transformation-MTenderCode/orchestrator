package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindContractDocumentIdAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.toDomain
import org.springframework.stereotype.Component

@Component
class ContractingFindContractDocumentIdDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, FindContractDocumentIdAction.Result>(
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
    ): Result<Reply<FindContractDocumentIdAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contract = context.getContractIfOnlyOne().orForwardFail { return it }

        return contractingClient.findContractDocumentId(
            id = commandId,
            params = FindContractDocumentIdAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                processInitiator = processInfo.processInitiator ,
                contracts = listOf(
                    FindContractDocumentIdAction.Params.Contract(id = contract.id)
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<FindContractDocumentIdAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull ?: return MaybeFail.none()

        val receivedContracts = data.contracts
            .map { it.toDomain() }
            .let { Contracts(it) }

        context.contracts = context.contracts updateBy receivedContracts

        return MaybeFail.none()
    }

}
