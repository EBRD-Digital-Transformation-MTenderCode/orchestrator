package com.procurement.orchestrator.infrastructure.bpms.delegate.qualification

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.QualificationClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.EnumElementProvider.Companion.keysAsStrings
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.State
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.qualification.QualificationStatus
import com.procurement.orchestrator.domain.model.qualification.QualificationStatusDetails
import com.procurement.orchestrator.domain.model.qualification.Qualifications
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.delegate.parameter.StateParameter
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.qualification.action.FindQualificationIdsAction
import org.springframework.stereotype.Component

@Component
class QualificationFindQualificationIdsDelegate(
    logger: Logger,
    private val qualificationClient: QualificationClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<QualificationFindQualificationIdsDelegate.Parameters, FindQualificationIdsAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    companion object {

        private const val NAME_PARAMETER_OF_STATES = "states"
        private const val NAME_PARAMETER_OF_STATUS = "status"
        private const val NAME_PARAMETER_OF_STATUS_DETAILS = "statusDetails"

        fun parseState(value: String): Result<State<QualificationStatus, QualificationStatusDetails>, Fail.Incident.Bpmn.Parameter> =
            StateParameter.parse(value)
                .let { result ->
                    State(
                        status = result.status
                            ?.let { status ->
                                QualificationStatus.orNull(status)
                                    ?: return Result.failure(
                                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                                            name = NAME_PARAMETER_OF_STATUS,
                                            expectedValues = QualificationStatus.allowedElements.keysAsStrings(),
                                            actualValue = status
                                        )
                                    )
                            },
                        statusDetails = result.statusDetails
                            ?.let { statusDetails ->
                                QualificationStatusDetails.orNull(statusDetails)
                                    ?: return Result.failure(
                                        Fail.Incident.Bpmn.Parameter.UnknownValue(
                                            name = NAME_PARAMETER_OF_STATUS_DETAILS,
                                            expectedValues = QualificationStatusDetails.allowedElements.keysAsStrings(),
                                            actualValue = statusDetails
                                        )
                                    )
                            }
                    )
                }
                .asSuccess()
    }

    override fun parameters(parameterContainer: ParameterContainer): Result<Parameters, Fail.Incident.Bpmn.Parameter> {
        val states = parameterContainer.getListString(NAME_PARAMETER_OF_STATES)
            .orForwardFail { fail -> return fail }
            .map { state ->
                parseState(state)
                    .orForwardFail { fail -> return fail }
            }
        return success(Parameters(states = states))
    }

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Parameters
    ): Result<Reply<FindQualificationIdsAction.Result>, Fail.Incident> {

        val processInfo = context.processInfo

        val states = parameters.states
            .map {
                FindQualificationIdsAction.Params.State(status = it.status, statusDetails = it.statusDetails)
            }
        return qualificationClient.findQualificationIds(
            id = commandId,
            params = FindQualificationIdsAction.Params(
                cpid = processInfo.cpid!!,
                ocid = processInfo.ocid!!,
                states = states

            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Parameters,
        result: Option<FindQualificationIdsAction.Result>
    ): MaybeFail<Fail.Incident> {

        val data = result.orNull
            ?: return MaybeFail.none()

        val contextQualifications = context.qualifications
        val requestQualifications = data.map { Qualification(id = it) }

        val newQualifications = getNewElements(received = requestQualifications, known = contextQualifications)

        context.qualifications = Qualifications(contextQualifications + newQualifications)

        return MaybeFail.none()
    }

    class Parameters(val states: List<State<QualificationStatus, QualificationStatusDetails>>)
}
