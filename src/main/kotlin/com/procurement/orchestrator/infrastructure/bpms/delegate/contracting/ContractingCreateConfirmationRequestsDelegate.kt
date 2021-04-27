package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getContractIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetProcessMasterData
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequest
import com.procurement.orchestrator.domain.model.contract.confirmation.request.ConfirmationRequests
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationRequestsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationRequestsAction.ResponseConverter.Contract.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingCreateConfirmationRequestsDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<ContractingCreateConfirmationRequestsDelegate.Parameters, CreateConfirmationRequestsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val ROLE_PARAMETER_NAME = "role"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val role = parameterContainer.getString(ROLE_PARAMETER_NAME)
            .orForwardFail { fail -> return fail }
            .let {
                Role.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = ROLE_PARAMETER_NAME,
                            expectedValues = Role.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return Result.success(Parameters(role = role))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<CreateConfirmationRequestsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val contract = context.getContractIfOnlyOne()
            .orForwardFail { return it }
        val processMasterData = context.tryGetProcessMasterData()
            .orForwardFail { fail -> return fail }

        return contractingClient.createConfirmationRequests(
            id = commandId,
            params = CreateConfirmationRequestsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                role = parameters.role.key,
                contracts = listOf(
                    CreateConfirmationRequestsAction.Params.Contract(
                        id = contract.id,
                        documents = contract.documents.map { document ->
                            CreateConfirmationRequestsAction.Params.Contract.Document(document.id)
                        }
                    )
                ),
                access = processMasterData.access?.let { access ->
                    CreateConfirmationRequestsAction.Params.Access(
                        buyers = access.buyers.map { buyer ->
                            CreateConfirmationRequestsAction.Params.Access.Buyer(
                                id = buyer.id,
                                name = buyer.name,
                                owner = buyer.owner
                            )
                        },
                        procuringEntity = access.procuringEntity?.let { procuringEntity ->
                            CreateConfirmationRequestsAction.Params.Access.ProcuringEntity(
                                id = procuringEntity.id,
                                name = procuringEntity.name,
                                owner = procuringEntity.owner
                            )
                        }
                    )
                },
                dossier = processMasterData.dossier?.let { dossier ->
                    CreateConfirmationRequestsAction.Params.Dossier(
                        candidates = dossier.candidates.map { candidate ->
                            CreateConfirmationRequestsAction.Params.Dossier.Candidate(
                                owner = candidate.owner,
                                organizations = candidate.organizations.map { organization ->
                                    CreateConfirmationRequestsAction.Params.Dossier.Candidate.Organization(
                                        id = organization.id,
                                        name = organization.name
                                    )
                                }
                            )
                        }
                    )
                },
                submission = processMasterData.submission?.let { submission ->
                    CreateConfirmationRequestsAction.Params.Submission(
                        tenderers = submission.tenderers.map { tenderer ->
                            CreateConfirmationRequestsAction.Params.Submission.Tenderer(
                                owner = tenderer.owner,
                                organizations = tenderer.organizations.map { organization ->
                                    CreateConfirmationRequestsAction.Params.Submission.Tenderer.Organization(
                                        id = organization.id,
                                        name = organization.name
                                    )
                                }
                            )
                        }
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<CreateConfirmationRequestsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    ExternalServiceName.CONTRACTING,
                    ContractingCommands.CreateConfirmationRequests
                )
            )

        val receivedRequests = data.contracts
            .first()
            .confirmationRequests
            .map { it.toDomain() }
            .let { ConfirmationRequests(it) }

        val updatedContracts = context.getContractIfOnlyOne()
            .orForwardFail { return it.asMaybeFail }
            .copy(confirmationRequests = receivedRequests)
            .let { Contracts(it) }

        context.outcomes = createOutcomes(context, receivedRequests.first())
        context.contracts = updatedContracts

        return MaybeFail.none()
    }

    private fun createOutcomes(
        context: CamundaGlobalContext,
        confirmationRequest: ConfirmationRequest
    ): Outcomes {
        val platformId = confirmationRequest.requestGroups.first().owner
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = confirmationRequest.requestGroups
            .map { requestGroup -> Outcomes.Details.RequestGroup(id = requestGroup.id, token = requestGroup.token) }

        val updatedDetails = details.copy(requestGroups = newOutcomes)
        outcomes[platformId] = updatedDetails
        return outcomes
    }

    class Parameters(val role: Role)

    enum class Role(@JsonValue override val key: String) : EnumElementProvider.Key {

        PROCURING_ENTITY("procuringEntity"),
        BUYER("buyer"),
        SUPPLIER("supplier"),
        INVITED_CANDIDATE("invitedCandidate");

        override fun toString(): String = key

        companion object : EnumElementProvider<Role>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Role.orThrow(name)
        }
    }
}
