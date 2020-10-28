package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.extension.lotIds
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.LotStatus
import com.procurement.orchestrator.domain.model.lot.LotStatusDetails
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForLotsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
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
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val status: LotStatus = parameterContainer.getString(PARAMETER_NAME_STATUS)
            .orForwardFail { fail -> return fail }
            .let { status ->
                LotStatus.orNull(status)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_STATUS,
                            actualValue = status,
                            expectedValues = LotStatus.allowedElements.keysAsStrings()
                        )
                    )
            }

        val statusDetails: LotStatusDetails? = parameterContainer.getStringOrNull(PARAMETER_NAME_STATUS_DETAILS)
            .orForwardFail { fail -> return fail }
            ?.let { statusDetails ->
                LotStatusDetails.orNull(statusDetails)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_STATUS_DETAILS,
                            actualValue = statusDetails,
                            expectedValues = LotStatusDetails.allowedElements.keysAsStrings()
                        )
                    )
            }

        val location: Location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let { location ->
                Location.orNull(location)
                    ?: return failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            actualValue = location,
                            expectedValues = Location.allowedElements.keysAsStrings()
                        )
                    )
            }
        return Result.success(Parameters(status = status, statusDetails = statusDetails, location = location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo

        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val lots = when (parameters.location) {
            Location.TENDER -> tender.lots
                .map { lot ->
                    SetStateForLotsAction.Params.Lot(
                        id = lot.id,
                        status = parameters.status,
                        statusDetails = parameters.statusDetails
                    )
                }

            Location.TENDER_AMENDMENT -> tender.amendments
                .map { amendment ->
                    val entityId = amendment.relatedItem
                        ?: return failure(
                            Fail.Incident.Bpms.Context.Missing(
                                name = "relatedItem",
                                path = "tender.amendments[${amendment.id}].relatedItem"
                            )
                        )
                    val lotId = LotId.create(entityId)
                    SetStateForLotsAction.Params.Lot(
                        id = lotId,
                        status = parameters.status,
                        statusDetails = parameters.statusDetails
                    )
                }
        }

        return accessClient.setStateForLots(
            id = commandId,
            params = SetStateForLotsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                lots = lots
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<SetStateForLotsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.ACCESS, action = AccessCommands.SetStateForLots)
            )

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
                receivedLotByIds.getValue(id)
                    .let { lot ->
                        Lot(id = lot.id, status = lot.status, statusDetails = lot.statusDetails)
                    }
            }

        val updatedTender = tender.copy(
            lots = Lots(updatedLots + newLots)
        )
        context.tender = updatedTender

        return MaybeFail.none()
    }

    class Parameters(val status: LotStatus, val statusDetails: LotStatusDetails?, val location: Location)

    enum class Location(@JsonValue override val key: String) : EnumElementProvider.Key {

        TENDER("tender"),
        TENDER_AMENDMENT("tender.amendment");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Location.orThrow(name)
        }
    }
}
