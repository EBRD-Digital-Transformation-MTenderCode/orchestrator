package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.lotIds
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import org.springframework.stereotype.Component

@Component
class AccessGetLotStateByIdsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, GetLotStateByIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident> {

        val tender = context.tryGetTender()
            .doOnError { return failure(it) }
            .get

        val processInfo = context.processInfo
        return accessClient.getLotStateByIds(
            params = GetLotStateByIdsAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                ids = tender.lots
                    .map { lot -> lot.id as LotId.Permanent }
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: GetLotStateByIdsAction.Result
    ): MaybeFail<Fail.Incident> {

        val tender = context.tryGetTender()
            .doOnError { return MaybeFail.fail(it) }
            .get

        val receivedLotByIds: Map<LotId, GetLotStateByIdsAction.Result.Lot> = data.associateBy { it.id }
        val receivedLotIds = receivedLotByIds.keys
        val knowLotIds = tender.lotIds()

        val updatedLots = tender.lots
            .map { lot ->
                receivedLotByIds[lot.id]
                    ?.let { receivedLot ->
                        lot.copy(status = receivedLot.status, statusDetails = receivedLot.statusDetails)
                    }
                    ?: lot
            }
        val newLots = getNewElements(received = receivedLotIds, known = knowLotIds)
            .map { id ->
                receivedLotByIds.getValue(id).let { lot ->
                    Lot(id = lot.id, status = lot.status, statusDetails = lot.statusDetails)
                }
            }

        val updatedTender = tender.copy(
            lots = updatedLots + newLots
        )
        context.tender = updatedTender

        return MaybeFail.none()
    }
}
