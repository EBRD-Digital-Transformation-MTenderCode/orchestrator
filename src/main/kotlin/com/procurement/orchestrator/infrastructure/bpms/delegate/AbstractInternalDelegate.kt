package com.procurement.orchestrator.infrastructure.bpms.delegate

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.model.context.serialize
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.extension.asPropertyContainer
import com.procurement.orchestrator.infrastructure.bpms.extension.isUpdateGlobalContext
import com.procurement.orchestrator.infrastructure.bpms.extension.setResult
import com.procurement.orchestrator.infrastructure.bpms.extension.writeIncident
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

abstract class AbstractInternalDelegate<P, R : Any>(
    private val logger: Logger,
    private val transform: Transform,
    private val operationStepRepository: OperationStepRepository
) : JavaDelegate {

    companion object {
        private const val INTERNAL_INCIDENT_CODE = "InternalIncident"
    }

    final override fun execute(execution: DelegateExecution) {

        val parameters = parameters(ParameterContainer(execution))
            .orReturnFail { fail -> execution.throwIncident(fail) }
        val globalContext = CamundaGlobalContext(propertyContainer = execution.asPropertyContainer())

        val resultContext = ResultContext()
        val scope = GlobalScope + resultContext
        val result = runBlocking(context = scope.coroutineContext) { execute(globalContext, parameters) }
            .orReturnFail { fail -> execution.throwIncident(fail) }
            .also { data ->
                if (execution.isUpdateGlobalContext) {
                    updateGlobalContext(context = globalContext, parameters = parameters, data = data)
                        .doOnFail { fail -> execution.throwIncident(fail) }
                }
            }

        saveStep(globalContext, resultContext, execution)
            .doOnError { fail -> execution.throwIncident(fail) }

        execution.setResult(value = result)
    }

    protected abstract fun parameters(parameterContainer: ParameterContainer): Result<P, Fail.Incident.Bpmn.Parameter>

    protected abstract suspend fun execute(
        context: CamundaGlobalContext,
        parameters: P
    ): Result<Option<R>, Fail.Incident>

    protected abstract fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: P,
        data: R
    ): MaybeFail<Fail.Incident.Bpmn>

    private fun saveStep(
        globalContext: CamundaGlobalContext,
        resultContext: ResultContext,
        execution: DelegateExecution
    ): Result<Boolean, Fail.Incident> {
        val requestInfo = globalContext.requestInfo
        val processInfo = globalContext.processInfo

        val processId = execution.processInstanceId
        val taskId = execution.currentActivityId
        val stepDate = nowDefaultUTC()

        val request = if (resultContext.hasRequest) resultContext.request() else ""
        val response = if (resultContext.hasResponse) resultContext.response() else ""
        val serializedContext = globalContext.serialize(transform)
            .orForwardFail { fail -> return fail }

        return operationStepRepository.save(
            step = OperationStep(
                cpid = processInfo.cpid,
                operationId = requestInfo.operationId,
                processId = processId,
                taskId = taskId,
                stepDate = stepDate,
                request = request,
                response = response,
                context = serializedContext
            )
        )
    }

    private fun DelegateExecution.throwIncident(incident: Fail.Incident): Nothing {
        incident.logging(logger)
        writeIncident(
            Incident(
                id = this.processInstanceId,
                date = nowDefaultUTC().format(),
                level = incident.level.key,
                service = GlobalProperties.service
                    .let { service ->
                        Incident.Service(
                            id = service.id,
                            name = service.name,
                            version = service.version
                        )
                    },
                details = listOf(
                    Incident.Detail(
                        code = incident.code,
                        description = incident.description
                    )
                )
            )
        )
        throw BpmnError(INTERNAL_INCIDENT_CODE, this.currentActivityId)
    }
}
