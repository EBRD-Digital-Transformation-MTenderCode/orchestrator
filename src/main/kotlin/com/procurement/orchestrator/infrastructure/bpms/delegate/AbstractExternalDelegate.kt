package com.procurement.orchestrator.infrastructure.bpms.delegate

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.model.context.serialize
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.infrastructure.bpms.extension.asPropertyContainer
import com.procurement.orchestrator.infrastructure.bpms.extension.errors
import com.procurement.orchestrator.infrastructure.bpms.extension.incident
import com.procurement.orchestrator.infrastructure.bpms.extension.isUpdateGlobalContext
import com.procurement.orchestrator.infrastructure.bpms.extension.setResult
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import kotlin.coroutines.CoroutineContext

abstract class AbstractExternalDelegate<P, R : Any>(
    private val logger: Logger,
    private val transform: Transform,
    private val operationStepRepository: OperationStepRepository
) : JavaDelegate {

    companion object {
        private const val VALIDATION_ERROR_CODE = "ValidationError"
        private const val EXTERNAL_INCIDENT_CODE = "ExternalIncident"
        private const val INTERNAL_INCIDENT_CODE = "InternalIncident"
    }

    final override fun execute(execution: DelegateExecution) {

        val parameters = parameters(ParameterContainer(execution))
            .doOnError { fail -> execution.throwInternalIncident(fail) }
            .get
        val globalContext = CamundaGlobalContext(propertyContainer = execution.asPropertyContainer())

        val resultContext = ResultContext()
        val scope = GlobalScope + resultContext
        val reply = runBlocking(context = scope.coroutineContext) {
            execute(globalContext, parameters)
                .also { reply ->
                    reply.result.also { data ->
                        if (execution.isUpdateGlobalContext) {
                            updateGlobalContext(context = globalContext, parameters = parameters, data = data)
                                .doOnFail { return@runBlocking failure(it) }
                            appendContextForSave(globalContext, coroutineContext)
                                .doOnFail { return@runBlocking failure(it) }
                        } else {
                            appendContextForSave(globalContext, coroutineContext)
                                .doOnFail { return@runBlocking failure(it) }
                        }
                    }
                }
        }
            .doOnError { fail -> execution.throwInternalIncident(fail) }
            .get

        val requestInfo = globalContext.requestInfo
        val operationId = requestInfo.operationId

        val processInfo = globalContext.processInfo
        val cpid = processInfo.cpid
        val processId = execution.processInstanceId
        val taskId = execution.currentActivityId
        val stepDate = nowDefaultUTC()

        val request = if (resultContext.hasRequest) resultContext.request() else ""
        val response = if (resultContext.hasResponse) resultContext.response() else ""
        val updatedContext = if (resultContext.hasGlobalContext) resultContext.globalContext() else ""

        operationStepRepository
            .save(
                step = OperationStep(
                    cpid = cpid,
                    operationId = operationId,
                    processId = processId,
                    taskId = taskId,
                    stepDate = stepDate,
                    request = request,
                    response = response,
                    context = updatedContext
                )
            )
            .doOnError { fail -> execution.throwInternalIncident(fail) }

        //TODO Check duplicate
        when (reply.result) {
            is Reply.Result.Success<R> -> execution.setResult(value = reply.result.value)
            is Reply.Result.Errors -> execution.throwReplyError(errors = reply.result)
            is Reply.Result.Incident -> execution.throwExternalIncident(reply.result)
        }
    }

    protected abstract fun parameters(parameterContainer: ParameterContainer): Result<P, Fail.Incident.Bpmn.Parameter>

    protected abstract suspend fun execute(
        context: CamundaGlobalContext,
        parameters: P
    ): Result<Reply<R>, Fail.Incident>

    protected abstract fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: P,
        data: R
    ): MaybeFail<Fail.Incident>

    private fun appendContextForSave(globalContext: CamundaGlobalContext, coroutineContext: CoroutineContext): MaybeFail<Fail.Incident> = when (
        val resultSerialization = globalContext.serialize(transform)) {
        is Result.Success -> {
            val ctx = coroutineContext[ResultContext.Key]!!
            ctx.globalContext(resultSerialization.get)
            MaybeFail.none()
        }
        is Result.Failure -> MaybeFail.fail(resultSerialization.error)
    }

    private fun DelegateExecution.throwInternalIncident(incident: Fail.Incident): Nothing {
        incident.logging(logger)
        incident(
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

    private fun DelegateExecution.throwExternalIncident(incident: Reply.Result.Incident): Nothing {
        incident(
            Incident(
                id = incident.id,
                date = incident.date,
                level = incident.level.key,
                service = incident.service
                    .let { service ->
                        Incident.Service(
                            id = service.id,
                            name = service.name,
                            version = service.version
                        )
                    },
                details = incident.details
                    .map { detail ->
                        Incident.Detail(
                            code = detail.code,
                            description = detail.description,
                            metadata = detail.metadata
                        )
                    }
            )
        )
        throw BpmnError(EXTERNAL_INCIDENT_CODE, this.currentActivityId)
    }

    private fun DelegateExecution.throwReplyError(errors: Reply.Result.Errors): Nothing {
        val existErrors = this.errors()
        val newErrors = errors.map { error ->
            Errors.Error(
                code = error.code,
                description = error.description,
                details = error.details
                    .map { detail ->
                        Errors.Error.Detail(
                            id = detail.id,
                            name = detail.name
                        )
                    }
            )
        }
        this.errors(existErrors + newErrors)
        throw BpmnError(VALIDATION_ERROR_CODE)
    }
}
