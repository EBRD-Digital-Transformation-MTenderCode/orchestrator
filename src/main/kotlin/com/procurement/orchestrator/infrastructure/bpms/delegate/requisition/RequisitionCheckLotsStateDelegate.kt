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
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.access.AccessResponderProcessingDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.mdm.MdmEnrichCountryDelegate
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckLotsStateAction
import org.springframework.stereotype.Component

@Component
class RequisitionCheckLotsStateDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RequisitionCheckLotsStateDelegate.Parameters, Unit>(
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
    ): Result<Reply<Unit>, Fail.Incident> {

        val processInfo = context.processInfo
        val requestInfo = context.requestInfo

        return requisitionClient.checkLotsState(
            id = commandId,
            params = CheckLotsStateAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                country = requestInfo.country,
                pmd = processInfo.pmd,
                operationType = processInfo.operationType,
                tender = CheckLotsStateAction.Params.Tender(
                    when(parameters.location){
                        Location.BID -> context.bids?.details?.asSequence()
                            ?.flatMap { it.relatedLots.asSequence() }
                            ?.map { CheckLotsStateAction.Params.Tender.Lot(it) }
                            ?.toList()
                    }
                )
            )
        )
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
