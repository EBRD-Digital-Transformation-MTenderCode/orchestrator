package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.ValidateConfirmationResponseDataAction
import org.springframework.stereotype.Component

@Component
class ContractingValidateConfirmationResponseDataDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo

        return contractingClient.validateConfirmationResponseData(
            id = commandId,
            params = ValidateConfirmationResponseDataAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                contracts = context.contracts.map { contract ->
                    ValidateConfirmationResponseDataAction.Params.Contract(
                        id = contract.id,
                        confirmationResponses = contract.confirmationResponses.map { confirmationResponse ->
                            ValidateConfirmationResponseDataAction.Params.Contract.ConfirmationResponse(
                                id = confirmationResponse.id,
                                requestId = confirmationResponse.requestId,
                                type = confirmationResponse.type,
                                value = confirmationResponse.value,
                                relatedPerson = confirmationResponse.relatedPerson
                                    ?.let { person ->
                                        ValidateConfirmationResponseDataAction.Params.Contract.ConfirmationResponse.RelatedPerson(
                                            title = person.title,
                                            id = person.id,
                                            name = person.name,
                                            identifier = person.identifier?.let { identifier ->
                                                ValidateConfirmationResponseDataAction.Params.Contract.ConfirmationResponse.RelatedPerson.Identifier(
                                                    scheme = identifier.scheme,
                                                    id = identifier.id,
                                                    uri = identifier.uri
                                                )
                                            },
                                            businessFunctions = person.businessFunctions.map { businessFunction ->
                                                ValidateConfirmationResponseDataAction.Params.Contract.ConfirmationResponse.RelatedPerson.BusinessFunction(
                                                    id = businessFunction.id,
                                                    type = businessFunction.type,
                                                    period = businessFunction.period?.let { period ->
                                                        ValidateConfirmationResponseDataAction.Params.Contract.ConfirmationResponse.RelatedPerson.BusinessFunction.Period(
                                                            period.startDate
                                                        )
                                                    },
                                                    documents = businessFunction.documents.map { document ->
                                                        ValidateConfirmationResponseDataAction.Params.Contract.ConfirmationResponse.RelatedPerson.BusinessFunction.Document(
                                                            id = document.id,
                                                            title = document.title,
                                                            description = document.description,
                                                            documentType = document.documentType
                                                        )
                                                    },
                                                    jobTitle = businessFunction.jobTitle
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
    }
}
