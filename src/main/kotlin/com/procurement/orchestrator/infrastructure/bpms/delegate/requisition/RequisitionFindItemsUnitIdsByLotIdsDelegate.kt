package com.procurement.orchestrator.infrastructure.bpms.delegate.requisition

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.item.Item
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.unit.Unit
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.access.AccessResponderProcessingDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.mdm.MdmEnrichCountryDelegate
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindItemsByLotIdsAction
import org.springframework.stereotype.Component

@Component
class RequisitionFindItemsUnitIdsByLotIdsDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RequisitionFindItemsUnitIdsByLotIdsDelegate.Parameters, FindItemsByLotIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        const val PARAMETER_NAME_LOCATION: String = "location"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val location: Location = parameterContainer.getString(PARAMETER_NAME_LOCATION)
            .orForwardFail { fail -> return fail }
            .let { location ->
                Location.orNull(location)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = MdmEnrichCountryDelegate.PARAMETER_NAME_LOCATION,
                            actualValue = location,
                            expectedValues = MdmEnrichCountryDelegate.Location.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(location))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindItemsByLotIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return requisitionClient.findItemsByLotIds(
            id = commandId,
            params = FindItemsByLotIdsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = FindItemsByLotIdsAction.Params.Tender(
                    when (parameters.location) {
                        Location.BID -> context.bids?.details?.asSequence()
                            ?.flatMap { it.relatedLots.asSequence() }
                            ?.map { FindItemsByLotIdsAction.Params.Tender.Lot(it) }
                            ?.toList()
                    }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindItemsByLotIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val tender = context.tender ?: Tender()

        val receivedItemsIds = data.tender.items
            .map { item ->
                Item(
                    id = item.id,
                    unit = Unit(id = item.unit.id)
                )
            }
            .let {
                Items(it)
            }
        val updatedItems = tender.items updateBy receivedItemsIds

        context.tender = tender.copy(items = updatedItems)

        return MaybeFail.none()
    }

    class Parameters(val location: Location)

    enum class Location(override val key: String) : EnumElementProvider.Key {
        BID("bid");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = AccessResponderProcessingDelegate.Location.orThrow(name)
        }
    }
}
