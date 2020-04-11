package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.lotIds
import com.procurement.orchestrator.domain.extension.merge
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.State
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.parameter.StateParameter
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindLotIdsAction
import org.camunda.bpm.engine.delegate.BpmnError
import org.springframework.stereotype.Component

@Component
class AccessFindLotIdsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessFindLotIdsDelegate.Parameters, List<LotId>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {

        private const val NAME_PARAMETER_OF_STATES = "states"

        fun parseState(value: String): Result<State<LotStatus, LotStatusDetails>, String> = StateParameter.parse(value)
            .let { result ->
                State(
                    status = result.status
                        ?.let { status ->
                            LotStatus.tryOf(status)
                                .orForwardFail { fail -> return fail }
                        },
                    statusDetails = result.statusDetails
                        ?.let { statusDetails ->
                            LotStatusDetails.tryOf(statusDetails)
                                .orForwardFail { fail -> return fail }
                        }
                )
            }
            .asSuccess()
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val states = parameterContainer.getListString(NAME_PARAMETER_OF_STATES)
            .orForwardFail { fail -> return fail }
            .map { state ->
                when (val result = parseState(state)) {
                    is Result.Success -> result.get
                    is Result.Failure -> throw BpmnError(result.error)
                }
            }
        return success(Parameters(states = states))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<List<LotId>>, Fail.Incident> {

        val processInfo = context.processInfo
        return accessClient.findLotIds(
            id = commandId,
            params = FindLotIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                states = parameters.states
                    .map { state ->
                        FindLotIdsAction.Params.State(
                            status = state.status,
                            statusDetails = state.statusDetails
                        )
                    }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: List<LotId>
    ): MaybeFail<Fail.Incident> {
        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val knowLotIds = tender.lotIds()
        val receivedLotIds = data.toSet()
        val newLots = getNewElements(received = receivedLotIds, known = knowLotIds)
            .map { id -> Lot(id = id) }

        val updatedTender = tender.copy(
            lots = tender.lots.merge(newLots)
        )
        context.tender = updatedTender

        return MaybeFail.none()
    }

    class Parameters(val states: List<State<LotStatus, LotStatusDetails>>)
}
