package com.procurement.orchestrator.infrastructure.bpms.delegate.contracting

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.ContractingClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.State
import com.procurement.orchestrator.domain.model.amendment.AmendmentRelatesTo
import com.procurement.orchestrator.domain.model.can.CanStatus
import com.procurement.orchestrator.domain.model.can.CanStatusDetails
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.parameter.StateParameter
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import org.springframework.stereotype.Component

@Component
class ContractingFindCANIdsDelegate(
    logger: Logger,
    private val contractingClient: ContractingClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<ContractingFindCANIdsDelegate.Parameters, FindCANIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val NAME_PARAMETER_OF_STATUS = "status"
        private const val NAME_PARAMETER_OF_STATUS_DETAILS = "statusDetails"
        private const val NAME_PARAMETER_OF_STATES = "states"
        private const val LOCATION_OF_LOTS = "locationOfLots"
    }

    fun parseState(value: String): Result<State<CanStatus, CanStatusDetails>, Fail.Incident.Bpmn.Parameter> =
        StateParameter.parse(value)
            .let { result ->
                State(
                    status = result.status
                        ?.let { status ->
                            CanStatus.orNull(status)
                                ?: return Result.failure(
                                    Fail.Incident.Bpmn.Parameter.UnknownValue(
                                        name = NAME_PARAMETER_OF_STATUS,
                                        expectedValues = CanStatus.allowedElements.keysAsStrings(),
                                        actualValue = status
                                    )
                                )
                        },
                    statusDetails = result.statusDetails
                        ?.let { statusDetails ->
                            CanStatusDetails.orNull(statusDetails)
                                ?: return Result.failure(
                                    Fail.Incident.Bpmn.Parameter.UnknownValue(
                                        name = NAME_PARAMETER_OF_STATUS_DETAILS,
                                        expectedValues = CanStatusDetails.allowedElements.keysAsStrings(),
                                        actualValue = statusDetails
                                    )
                                )
                        }
                )
            }
            .asSuccess()

    override fun parameters(parameterContainer: ParameterContainer)
        : Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val states = parameterContainer.getListString(NAME_PARAMETER_OF_STATES)
            .orForwardFail { fail -> return fail }
            .map { state ->
                parseState(state)
                    .orForwardFail { fail -> return fail }
            }

        val locationOfLots = parameterContainer.getStringOrNull(LOCATION_OF_LOTS)
            .orForwardFail { fail -> return fail }
            ?.let { value ->
                LocationOfLots.orNull(key = value)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "locationOfLots",
                            expectedValues = LocationOfLots.allowedElements.keysAsStrings(),
                            actualValue = value
                        )
                    )
            }

        return Parameters(states = states, locationOfLots = locationOfLots)
            .asSuccess()
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindCANIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { incident -> return incident }

        val lotIds: List<LotId> =
            if (parameters.locationOfLots != null) {
                when (parameters.locationOfLots) {
                    LocationOfLots.TENDER_LOTS -> {
                        tender.lots.map { it.id }
                    }
                    LocationOfLots.AMENDMENT_RELATED_ITEM -> {
                        val amendment = tender.getAmendmentIfOnlyOne()
                            .orForwardFail { incident -> return incident }

                        val relatedItem = amendment.relatedItem
                            ?: return Result.failure(Fail.Incident.Bpms.Context.Missing(name = "relatedItem"))

                        val relatesTo = amendment.relatesTo
                            ?: return Result.failure(Fail.Incident.Bpms.Context.Missing(name = "relatesTo"))

                        if (relatesTo == AmendmentRelatesTo.LOT) {
                            listOf(LotId.create(relatedItem))
                        } else {
                            return Result.failure(
                                Fail.Incident.Bpms.Context.UnexpectedRelatedEntity(
                                    path = "tender.amendment.relatesTo",
                                    name = "relatesTo",
                                    actual = relatesTo.key,
                                    expected = AmendmentRelatesTo.LOT.key
                                )
                            )
                        }
                    }
                }
            } else
                emptyList()


        return contractingClient.findCANIds(
            id = commandId,
            params = FindCANIdsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                states = parameters.states
                    .map { state ->
                        FindCANIdsAction.Params.State(
                            status = state.status,
                            statusDetails = state.statusDetails
                        )
                    },
                lotIds = lotIds
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindCANIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val contextContracts = context.contracts

        val knownContractsIds = contextContracts.map { it.id }
        val receivedCansIds = data.map { it.toString() }

        val newContracts = getNewElements(received = receivedCansIds, known = knownContractsIds)
            .map { Contract(id = it) }

        context.contracts = contextContracts.plus(newContracts)

        return MaybeFail.none()
    }

    class Parameters(val states: List<State<CanStatus, CanStatusDetails>>, val locationOfLots: LocationOfLots?)

    enum class LocationOfLots(@JsonValue override val key: String) : EnumElementProvider.Key {

        TENDER_LOTS("tender.lots"),
        AMENDMENT_RELATED_ITEM("amendment.relatedItem");

        override fun toString(): String = key

        companion object : EnumElementProvider<LocationOfLots>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = LocationOfLots.orThrow(name)
        }
    }
}
