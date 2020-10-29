package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.TenderStatus
import com.procurement.orchestrator.domain.model.tender.TenderStatusDetails
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.AccessCommands
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForTenderAction
import com.procurement.orchestrator.infrastructure.configuration.property.ExternalServiceName
import org.springframework.stereotype.Component

@Component
class AccessSetStateForTenderDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<AccessSetStateForTenderDelegate.Parameters, SetStateForTenderAction.Result>(
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
            .orForwardFail { fail -> return fail }
            .let { status ->
                TenderStatus.orNull(status)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_STATUS,
                            actualValue = status,
                            expectedValues = TenderStatus.allowedElements.keysAsStrings()
                        )
                    )
            }
        val statusDetails = parameterContainer.getString(PARAMETER_NAME_STATUS_DETAILS)
            .orForwardFail { fail -> return fail }
            .let { statusDetails ->
                TenderStatusDetails.orNull(statusDetails)
                    ?: return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = PARAMETER_NAME_STATUS_DETAILS,
                            actualValue = statusDetails,
                            expectedValues = TenderStatusDetails.allowedElements.keysAsStrings()
                        )
                    )
            }

        return Result.success(Parameters(status = status, statusDetails = statusDetails))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<SetStateForTenderAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo
        return accessClient.setStateForTender(
            id = commandId,
            params = SetStateForTenderAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                tender = SetStateForTenderAction.Params.Tender(
                    status = parameters.status,
                    statusDetails = parameters.statusDetails
                )
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<SetStateForTenderAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.fail(
                Fail.Incident.Response.Empty(service = ExternalServiceName.ACCESS, action = AccessCommands.SetStateForTender)
            )

        context.tender = context.tender
            ?.copy(
                status = data.status,
                statusDetails = data.statusDetails
            )
            ?: Tender(
                status = data.status,
                statusDetails = data.statusDetails
            )

        return MaybeFail.none()
    }

    class Parameters(val status: TenderStatus, val statusDetails: TenderStatusDetails)
}
