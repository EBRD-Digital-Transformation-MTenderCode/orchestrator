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
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckLotsStateAction
import org.springframework.stereotype.Component

@Component
class AccessCheckLotsStateDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessCheckLotsStateDelegate.Parameters, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {
        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        val lots: List<CheckLotsStateAction.Params.Tender.Lot> = when (parameters.location) {
            Location.BID -> context.tryGetBids()
                .orForwardFail { return it }
                .getBidsDetailIfOnlyOne()
                .orForwardFail { return it }
                .getRelatedLotsIfOnlyOne()
                .orForwardFail { return it }
                .let { relatedLotId -> CheckLotsStateAction.Params.Tender.Lot(relatedLotId) }
                .let { lot -> listOf(lot) }

            null -> context.tryGetTender()
                .orForwardFail { return it }
                .lots
                .map { lot -> CheckLotsStateAction.Params.Tender.Lot(lot.id) }
        }

        return accessClient.checkLotsState(
            id = commandId,
            params = CheckLotsStateAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                country = requestInfo.country,
                operationType = processInfo.operationType,
                pmd = processInfo.pmd,
                tender = CheckLotsStateAction.Params.Tender(lots = lots)
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<Unit>
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()

    class Parameters(
        val location: Location?
    )

    enum class Location(override val key: String) : EnumElementProvider.Key {

        BID("bid");

        override fun toString(): String = key

        companion object : EnumElementProvider<Location>(info = info())
    }
}
