package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.lotIds
import com.procurement.orchestrator.domain.extension.merge
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotIdsAction
import org.camunda.bpm.engine.delegate.BpmnError
import org.springframework.stereotype.Component

@Component
class AccessGetLotIdsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessGetLotIdsDelegate.Parameters, List<LotId>>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val STATUS = "status"
        private const val STATUS_DETAILS = "statusDetails"

        private val patternStatus =
            """[,]*[ ]*(status[ ]*=[ ]*(?<$STATUS>[a-zA-Z]*))?[ ]*[,]*""".toRegex()
        private val patternStatusDetails =
            """[,]*[ ]*(statusDetails[ ]*=[ ]*(?<$STATUS_DETAILS>[a-zA-Z]*))?[ ]*[,]*""".toRegex()

        fun parseState(value: String): Result<Pair<LotStatus?, LotStatusDetails?>, String> {
            val status = patternStatus.matchEntire(value)
                ?.let { result ->
                    val groups = result.groups as MatchNamedGroupCollection
                    LotStatus.tryOf(groups[STATUS]!!.value)
                        .doOnError { return failure(it) }
                        .get
                }

            val statusDetails = patternStatusDetails.matchEntire(value)
                ?.let { result ->
                    val groups = result.groups as MatchNamedGroupCollection
                    LotStatusDetails.tryOf(groups[STATUS_DETAILS]!!.value)
                        .doOnError { return failure(it) }
                        .get
                }
            return success(status to statusDetails)
        }
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val states = parameterContainer.getListString("states")
            .doOnError { return failure(it) }
            .get
        return success(Parameters(states = states))
    }

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<List<LotId>>, Fail.Incident> {
        val states = parameters.states
            .map { state ->
                when (val result = parseState(state)) {
                    is Result.Success -> result.get
                    is Result.Failure -> throw BpmnError(result.error)
                }
            }

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid
        return accessClient.getLotIds(
            params = GetLotIdsAction.Params(
                cpid = cpid,
                ocid = ocid,
                states = states.map {
                    GetLotIdsAction.Params.State(
                        status = it.first,
                        statusDetails = it.second
                    )
                }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: List<LotId>
    ): MaybeFail<Fail.Incident.Bpmn> {
        val tender = context.tender
            ?: Fail.Incident.Bpe(description = "The global context does not contain a 'Tender' object.")
                .throwIncident()

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

    class Parameters(val states: List<String>)
}
