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
import com.procurement.orchestrator.domain.model.tender.ProcurementMethodModalities
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.access.AccessResponderProcessingDelegate
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindProcurementMethodModalitiesAction
import org.springframework.stereotype.Component

@Component
class RequisitionFindProcurementMethodModalitiesDelegate(
    logger: Logger,
    private val requisitionClient: RequisitionClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<RequisitionFindProcurementMethodModalitiesDelegate.Parameters, FindProcurementMethodModalitiesAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {
    companion object {
        const val PARAMETER_NAME_VALUES: String = "values"
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val values = parameterContainer.getListString(PARAMETER_NAME_VALUES)
            .orForwardFail { fail -> return fail }
            .map { location ->
                Value.orNull(location)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_VALUES,
                            actualValue = location,
                            expectedValues = Value.allowedElements.keysAsStrings()
                        )
                    )
            }
        return success(Parameters(values))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindProcurementMethodModalitiesAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        return requisitionClient.findProcurementMethodModalities(
            id = commandId,
            params = FindProcurementMethodModalitiesAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
                tender = FindProcurementMethodModalitiesAction.Params.Tender(
                    parameters.values.map { it.key }
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindProcurementMethodModalitiesAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val tender = context.tender ?: Tender()

        val receivedModalities = data.tender.procurementMethodModalities.toSet()
        val knownModalities = tender.procurementMethodModalities.toSet()
        val updatedModalities = ProcurementMethodModalities((knownModalities + receivedModalities).toList())

        context.tender = tender.copy(procurementMethodModalities = updatedModalities)

        return MaybeFail.none()
    }

    class Parameters(val values: List<Value>)

    enum class Value(override val key: String) : EnumElementProvider.Key {
        REQUIRES_ELECTRONIC_CATALOGUE("requiresElectronicCatalogue"),
        ELECTRONIC_AUCTION("electronicAuction");

        override fun toString(): String = key

        companion object : EnumElementProvider<Value>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = AccessResponderProcessingDelegate.Location.orThrow(name)
        }
    }
}
