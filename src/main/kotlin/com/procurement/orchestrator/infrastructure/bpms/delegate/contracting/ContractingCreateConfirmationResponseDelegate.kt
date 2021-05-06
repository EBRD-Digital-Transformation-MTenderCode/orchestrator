package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationResponseAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingCreateConfirmationResponseDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreateConfirmationResponseAction.Result>(
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
    ): Result<Reply<CreateConfirmationResponseAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contract = context.getContractIfOnlyOne()
            .orForwardFail { return it }

        return contractingClient.createConfirmationResponse(
            id = commandId,
            params = CreateConfirmationResponseAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                date = context.requestInfo.timestamp,
                contracts = listOf(
                    CreateConfirmationResponseAction.Params.Contract(
                        id = contract.id,
                        confirmationResponses = contract.confirmationResponses.map { confirmationResponse ->
                            CreateConfirmationResponseAction.Params.Contract.ConfirmationResponse(
                                id = confirmationResponse.id,
                                type = confirmationResponse.type,
                                value = confirmationResponse.value,
                                requestId = confirmationResponse.requestId,
                                relatedPerson = confirmationResponse.relatedPerson?.let { person ->
                                    CreateConfirmationResponseAction.Params.Contract.ConfirmationResponse.RelatedPerson(
                                        id = person.id,
                                        title = person.title,
                                        name = person.name,
                                        identifier = person.identifier?.let { identifier ->
                                            CreateConfirmationResponseAction.Params.Contract.ConfirmationResponse.RelatedPerson.Identifier(
                                                id = identifier.id,
                                                uri = identifier.uri,
                                                scheme = identifier.scheme
                                            )
                                        },
                                        businessFunctions = person.businessFunctions.map { businessFunction ->
                                            CreateConfirmationResponseAction.Params.Contract.ConfirmationResponse.RelatedPerson.BusinessFunction(
                                                id = businessFunction.id,
                                                jobTitle = businessFunction.jobTitle,
                                                type = businessFunction.type,
                                                period = businessFunction.period?.let { period ->
                                                    CreateConfirmationResponseAction.Params.Contract.ConfirmationResponse.RelatedPerson.BusinessFunction.Period(
                                                        period.startDate
                                                    )
                                                },
                                                documents = businessFunction.documents.map { document ->
                                                    CreateConfirmationResponseAction.Params.Contract.ConfirmationResponse.RelatedPerson.BusinessFunction.Document(
                                                        id = document.id,
                                                        title = document.title,
                                                        documentType = document.documentType,
                                                        description = document.description
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreateConfirmationResponseAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    ExternalServiceName.CONTRACTING,
                    ContractingCommands.CreateConfirmationResponse
                )
            )

        val receivedContract = data.contracts.first().toDomain()

        val updatedContracts = context.getContractIfOnlyOne()
            .orForwardFail { return it.asMaybeFail }
            .updateBy(receivedContract)
            .let { Contracts(it) }

        context.outcomes = createOutcomes(context, receivedContract)
        context.contracts = updatedContracts

        return MaybeFail.none()
    }

    private fun createOutcomes(
        context: CamundaGlobalContext,
        contract: Contract
    ): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = contract.confirmationResponses
            .map { confirmationResponse -> Outcomes.Details.ConfirmationResponse(id = confirmationResponse.id) }

        val updatedDetails = details.copy(confirmationResponses = newOutcomes)
        outcomes[platformId] = updatedDetails
        return outcomes
    }
}
