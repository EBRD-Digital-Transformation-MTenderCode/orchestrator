package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getBidsDetailIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRelatedLotsIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.tryGetBids
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetItemsByLotIdsAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessGetItemIdsByLotsDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessGetItemIdsByLotsDelegate.Parameters, GetItemsByLotIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {
        private const val PARAMETER_NAME_LOCATION = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location? = parameterContainer.getStringOrNull(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            ?.let {
                Location.orNull(it)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_LOCATION,
                            expectedValues = Location.allowedElements.keysAsStrings(),
                            actualValue = it
                        )
                    )
            }

        return Result.success(Parameters(location = location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<GetItemsByLotIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val lots: List<GetItemsByLotIdsAction.Params.Tender.Lot> = when (parameters.location) {
            Location.BID -> context.tryGetBids()
                .orForwardFail { return it }
                .getBidsDetailIfOnlyOne()
                .orForwardFail { return it }
                .getRelatedLotsIfOnlyOne()
                .orForwardFail { return it }
                .let { relatedLotId -> GetItemsByLotIdsAction.Params.Tender.Lot(relatedLotId) }
                .let { lot -> listOf(lot) }

            null -> context.tryGetTender()
                .orForwardFail { return it }
                .lots
                .map { lot -> GetItemsByLotIdsAction.Params.Tender.Lot(lot.id) }
        }

        return accessClient.getItemsByLotIdsAction(
            id = commandId,
            params = GetItemsByLotIdsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = GetItemsByLotIdsAction.Params.Tender(lots = lots)
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<GetItemsByLotIdsAction.Result>
    ): MaybeFail<Fail.Incident> {
        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(
                    service = ExternalServiceName.ACCESS,
                    action = AccessCommands.GetItemsByLotIds
                )
            )

        val receivedItems = data.tender.items
            .map { Item(it.id) }
            .let { Items(it) }
        val tender = context.tender ?: Tender()
        context.tender = tender.copy(items = receivedItems)

        return MaybeFail.none()
    }

    class Parameters(
        val location: Location?
    )

    enum class Location(override val key: String) : EnumElementProvider.Key {

        BID("bid");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
