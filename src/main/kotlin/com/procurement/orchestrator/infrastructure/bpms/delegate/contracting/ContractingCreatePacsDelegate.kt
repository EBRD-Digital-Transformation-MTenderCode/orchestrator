package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.model.context.members.Outcomes
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.contract.Contracts
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.ContractingCommands
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreatePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.toDomain
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class ContractingCreatePacsDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, CreatePacsAction.Result>(
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
    ): Result<Reply<CreatePacsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val tender = context.tryGetTender()
            .orForwardFail { return it }

        return contractingClient.createPacs(
            id = commandId,
            params = CreatePacsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                date = requestInfo.timestamp,
                owner = requestInfo.owner,
                awards = context.awards.map { award ->
                    CreatePacsAction.Params.Award(
                        id = award.id,
                        suppliers = award.suppliers.map { supplier ->
                            CreatePacsAction.Params.Award.Supplier(
                                id = supplier.id,
                                name = supplier.name
                            )
                        }
                    )
                },
                bids = context.bids?.details
                    ?.map { bid ->
                        CreatePacsAction.Params.Bids.Detail(
                            id = bid.id,
                            tenderers = bid.tenderers
                                .map { tenderer ->
                                    CreatePacsAction.Params.Bids.Detail.Tenderer(
                                        id = tenderer.id,
                                        name = tenderer.name
                                    )
                                },
                            requirementResponses = bid.requirementResponses
                                .map { requirementResponse ->
                                    CreatePacsAction.Params.Bids.Detail.RequirementResponse(
                                        id = requirementResponse.id,
                                        value = requirementResponse.value,
                                        period = requirementResponse.period
                                            ?.let { period ->
                                                CreatePacsAction.Params.Bids.Detail.RequirementResponse.Period(
                                                    startDate = period.startDate,
                                                    endDate = period.endDate
                                                )
                                            },
                                        requirement = requirementResponse.requirement
                                            ?.let { requirement ->
                                                CreatePacsAction.Params.Bids.Detail.RequirementResponse.Requirement(
                                                    requirement.id
                                                )
                                            }
                                    )
                                }
                        )
                    }
                    ?.let { CreatePacsAction.Params.Bids(it) },
                tender = tender.let { tender ->
                    CreatePacsAction.Params.Tender(
                        lots = tender.lots.map { lot ->
                            CreatePacsAction.Params.Tender.Lot(lot.id)
                        },
                        criteria = tender.criteria.map { criterion ->
                            CreatePacsAction.Params.Tender.Criteria(
                                id = criterion.id,
                                title = criterion.title,
                                relatedItem = criterion.relatedItem,
                                relatesTo = criterion.relatesTo,
                                requirementGroups = criterion.requirementGroups.map { requirementGroup ->
                                    CreatePacsAction.Params.Tender.Criteria.RequirementGroup(
                                        id = requirementGroup.id,
                                        requirements = requirementGroup.requirements.map { requirement ->
                                            CreatePacsAction.Params.Tender.Criteria.RequirementGroup.Requirement(
                                                id = requirement.id,
                                                title = requirement.title
                                            )
                                        }
                                    )
                                }
                            )
                        },
                        targets = tender.targets.map { target ->
                            CreatePacsAction.Params.Tender.Target(
                                id = target.id,
                                observations = target.observations.map { observation ->
                                    CreatePacsAction.Params.Tender.Target.Observation(
                                        id = observation.id,
                                        relatedRequirementId = observation.relatedRequirementId,
                                        unit = observation.unit?.let { unit ->
                                            CreatePacsAction.Params.Tender.Target.Observation.Unit(
                                                id = unit.id,
                                                name = unit.name
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

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        result: Option<CreatePacsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    ExternalServiceName.CONTRACTING,
                    ContractingCommands.CreatePacs
                )
            )

        val receivedContracts = data.contracts
            .map { contract -> contract.toDomain() }
            .let { Contracts(it) }

        context.contracts = receivedContracts
        context.outcomes = createOutcomes(context, receivedContracts, data.token)

        return MaybeFail.none()
    }

    private fun createOutcomes(
        context: CamundaGlobalContext,
        contracts: Contracts,
        token: Token?
    ): Outcomes {
        val platformId = context.requestInfo.platformId
        val outcomes = context.outcomes ?: Outcomes()
        val details = outcomes[platformId] ?: Outcomes.Details()

        val newOutcomes = contracts
            .map { contract -> Outcomes.Details.Contract(id = contract.id, token = token) }

        val updatedDetails = details.copy(contracts = newOutcomes)
        outcomes[platformId] = updatedDetails
        return outcomes
    }
}
