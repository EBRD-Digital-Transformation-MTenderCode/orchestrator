package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.lotIds
import com.procurement.orchestrator.domain.extension.merge
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForLotsAction
import org.springframework.stereotype.Component

@Component
class AccessSetStateForLotsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessSetStateForLotsDelegate.Parameters, SetStateForLotsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        private const val PARAMETER_NAME_STATUS = "status"
        private const val PARAMETER_NAME_STATUS_DETAILS = "statusDetails"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val status = parameterContainer.getString(PARAMETER_NAME_STATUS)
            .orReturnFail { return Result.failure(it) }
            .let { status ->
                when (val result = LotStatus.tryOf(status)) {
                    is Result.Success -> result.get
                    is Result.Failure -> return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "status",
                            actualValue = status,
                            expectedValues = LotStatus.allowedElements.keysAsStrings()
                        )
                    )
                }
            }
        val statusDetails = parameterContainer.getString(PARAMETER_NAME_STATUS_DETAILS)
            .orReturnFail { return Result.failure(it) }
            .let { statusDetails ->
                when (val result = LotStatusDetails.tryOf(statusDetails)) {
                    is Result.Success -> result.get
                    is Result.Failure -> return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "statusDetails",
                            actualValue = statusDetails,
                            expectedValues = LotStatusDetails.allowedElements.keysAsStrings()
                        )
                    )
                }
            }

        return Result.success(Parameters(status = status, statusDetails = statusDetails))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orReturnFail { return Result.failure(it) }

        return accessClient.setStateForLots(
            id = commandId,
            params = SetStateForLotsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                lots = tender.lots
                    .map { lot ->
                        SetStateForLotsAction.Params.Lot(
                            id = lot.id as LotId.Permanent,
                            statusDetails = parameters.statusDetails,
                            status = parameters.status
                        )
                    }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        data: SetStateForLotsAction.Result
    ): MaybeFail<Fail.Incident> {

        val tender = context.tryGetTender()
            .orReturnFail { return MaybeFail.fail(it) }

        val receivedLotByIds: Map<LotId, SetStateForLotsAction.Result.Lot> = data.associateBy { it.id }

        val updatedLots = tender.lots
            .map { lot ->
                receivedLotByIds[lot.id]
                    ?.let { receivedLot ->
                        lot.copy(status = receivedLot.status, statusDetails = receivedLot.statusDetails)
                    }
                    ?: lot
            }

        val receivedLotIds = receivedLotByIds.keys
        val knowLotIds = tender.lotIds()

        val newLots = getNewElements(received = receivedLotIds, known = knowLotIds)
            .map { id ->
                receivedLotByIds.getValue(id).let { lot ->
                    Lot(id = lot.id, status = lot.status, statusDetails = lot.statusDetails)
                }
            }

        val updatedTender = tender.copy(
            lots = updatedLots.merge(newLots)
        )
        context.tender = updatedTender

        return MaybeFail.none()
    }

    class Parameters(val status: LotStatus, val statusDetails: LotStatusDetails)
}