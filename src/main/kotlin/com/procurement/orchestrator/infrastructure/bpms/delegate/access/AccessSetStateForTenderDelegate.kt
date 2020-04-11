package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.model.tender.TenderStatus
import com.procurement.orchestrator.domain.model.tender.TenderStatusDetails
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForTenderAction
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
                when (val result = TenderStatus.tryOf(status)) {
                    is Result.Success -> result.get
                    is Result.Failure -> return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "status",
                            actualValue = status,
                            expectedValues = TenderStatus.allowedElements.keysAsStrings()
                        )
                    )
                }
            }
        val statusDetails = parameterContainer.getString(PARAMETER_NAME_STATUS_DETAILS)
            .orForwardFail { fail -> return fail }
            .let { statusDetails ->
                when (val result = TenderStatusDetails.tryOf(statusDetails)) {
                    is Result.Success -> result.get
                    is Result.Failure -> return Result.failure(
                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                            name = "statusDetails",
                            actualValue = statusDetails,
                            expectedValues = TenderStatusDetails.allowedElements.keysAsStrings()
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
    ): Result<Reply<SetStateForTenderAction.Result>, Fail.Incident> {
        val processInfo = context.processInfo
        return accessClient.setStateForTender(
            id = commandId,
            params = SetStateForTenderAction.Params(
                cpid = processInfo.cpid,
                ocid = processInfo.ocid,
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
        data: SetStateForTenderAction.Result
    ): MaybeFail<Fail.Incident> {
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
