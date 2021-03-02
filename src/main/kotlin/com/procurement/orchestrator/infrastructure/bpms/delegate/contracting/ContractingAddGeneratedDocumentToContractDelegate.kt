package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddGeneratedDocumentToContractAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingAddGeneratedDocumentToContractDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, AddGeneratedDocumentToContractAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        Unit.asSuccess()

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<AddGeneratedDocumentToContractAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return contractingClient.addGeneratedDocumentToContract(
            id = commandId,
            params = AddGeneratedDocumentToContractAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                documentInitiator = processInfo.documentInitiator,
                contracts = context.contracts.map { contract -> contract.toRequest() }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<AddGeneratedDocumentToContractAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    ExternalServiceName.CONTRACTING,
                    ContractingCommands.AddGeneratedDocumentToContract
                )
            )

        val receivedContracts = Contracts(data.contracts.map { it.toDomain() })
        context.contracts = context.contracts.updateBy(receivedContracts)

        return MaybeFail.none()
    }

    private fun Contract.toRequest() =
        AddGeneratedDocumentToContractAction.Params.Contract(id = this.id, documents = this.documents.map { it.toRequest() })

    private fun Document.toRequest() =
        AddGeneratedDocumentToContractAction.Params.Contract.Document(id = this.id)
}
