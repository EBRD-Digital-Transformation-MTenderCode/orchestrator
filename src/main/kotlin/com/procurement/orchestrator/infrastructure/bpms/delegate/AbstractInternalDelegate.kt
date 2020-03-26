package com.procurement.orchestrator.infrastructure.bpms.delegate

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.serialize
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.infrastructure.bpms.extension.asPropertyContainer
import com.procurement.orchestrator.infrastructure.bpms.extension.isUpdateGlobalContext
import com.procurement.orchestrator.infrastructure.bpms.extension.setResult
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.pvm.PvmException

abstract class AbstractInternalDelegate<P, R : Any>(
    private val logger: Logger,
    private val transform: Transform,
    private val operationStepRepository: OperationStepRepository
) : JavaDelegate {

    final override fun execute(execution: DelegateExecution) {

        val parameters = parameters(ParameterContainer(execution))
            .doOnError { fail -> fail.throwBpmnIncident() }
            .get
        val context = CamundaGlobalContext(propertyContainer = execution.asPropertyContainer())

        val resultContext = ResultContext()
        val scope = GlobalScope + resultContext
        val result = runBlocking(context = scope.coroutineContext) {
            execute(context, parameters)
                .also { data ->
                    if (execution.isUpdateGlobalContext) {
                        val resultUpdate =
                            updateGlobalContext(context = context, parameters = parameters, data = data)
                        when (resultUpdate) {
                            is MaybeFail.None ->
                                when (val resultSerialization = context.serialize(transform)) {
                                    is Result.Success -> {
                                        val ctx = coroutineContext[ResultContext.Key]!!
                                        ctx.globalContext(resultSerialization.get)
                                    }
                                    is Result.Failure -> return@runBlocking failure(resultSerialization.error)
                                }

                            is MaybeFail.Fail -> return@runBlocking failure(resultUpdate.error)
                        }
                    }
                }
        }
            .doOnError { fail -> fail.throwBpmnIncident() }
            .get

        val requestInfo = context.requestInfo
        val processInfo = context.processInfo

        val processId = execution.processInstanceId
        val taskId = execution.currentActivityId
        val stepDate = nowDefaultUTC()

        val request = if (resultContext.hasRequest) resultContext.request() else ""
        val response = if (resultContext.hasResponse) resultContext.response() else ""
        val updatedContext = if (resultContext.hasGlobalContext) resultContext.globalContext() else ""

        operationStepRepository
            .save(
                step = OperationStep(
                    cpid = processInfo.cpid,
                    operationId = requestInfo.operationId,
                    processId = processId,
                    taskId = taskId,
                    stepDate = stepDate,
                    request = request,
                    response = response,
                    context = updatedContext
                )
            )
            .doOnError { fail -> fail.throwBpmnIncident() }

        execution.setResult(value = result)
    }

    protected abstract fun parameters(parameterContainer: ParameterContainer): Result<P, Fail.Incident.Bpmn.Parameter>

    protected abstract suspend fun execute(
        context: CamundaGlobalContext,
        parameters: P
    ): Result<R, Fail.Incident>

    protected abstract fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: P,
        data: R
    ): MaybeFail<Fail.Incident.Bpmn>

    private fun Fail.Incident.throwBpmnIncident(): Nothing {
        logging(logger)
        throw PvmException("${this.level.name} INCIDENT: '${this.description}'")
    }

    fun Fail.Incident.Bpe.throwIncident(): Nothing {
        logging(logger)
        throw PvmException("${this.level.name} INCIDENT: '${this.description}'")
    }
}
