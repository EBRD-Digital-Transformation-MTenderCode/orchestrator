package com.procurement.orchestrator.infrastructure.bpms.delegate

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.application.model.context.members.Errors
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
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.pvm.PvmException

abstract class AbstractExternalDelegate<P, R : Any>(
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
        val reply = runBlocking(context = scope.coroutineContext) {
            execute(context, parameters)
                .also { reply ->
                    reply.result.also { data ->
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
        }.doOnError { fail -> fail.throwBpmnIncident() }
            .get

        val requestInfo = context.requestInfo
        val operationId = requestInfo.operationId

        val processInfo = context.processInfo
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
            .doOnError { fail -> fail.throwBpmnIncident() }

        //TODO Check duplicate
        when (reply.result) {
            is Reply.Result.Success<R> -> execution.setResult(value = reply.result.value)
            is Reply.Result.Errors -> {
                context.appendErrors(errors = reply.result)
                throwReplyError()
            }
            is Reply.Result.Incident -> reply.result.throwReplyIncident()
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
    ): MaybeFail<Fail.Incident.Bpmn>

    private fun Fail.Incident.throwBpmnIncident(): Nothing {
        logging(logger)
        throw PvmException("${this.level.name} INCIDENT: '${this.description}'")
    }

    fun Fail.Incident.Bpe.throwIncident(): Nothing {
        logging(logger)
        throw PvmException("${this.level.name} INCIDENT: '${this.description}'")
    }

    private fun GlobalContext.appendErrors(errors: Reply.Result.Errors) {
        val newErrors = errors.map { error ->
            Errors.Error(
                code = error.code,
                description = error.description,
                details = error.details
                    .map { detail ->
                        Errors.Error.Detail(
                            name = detail.name
                        )
                    }
            )
        }

        this.errors = Errors(
            values = (this.errors
                ?: emptyList<Errors.Error>()) + newErrors
        )
    }

    private fun throwReplyError(): Nothing {
        throw BpmnError("ValidationError")
    }

    private fun Reply.Result.Incident.throwReplyIncident(): Nothing {
        val incident = this.let { incident ->
            val detailsAsText = incident.details.joinToString { detail ->
                "code: '${detail.code}', description: '${detail.description}', metadata: '${detail.metadata}'"
            }
            "${incident.level.name} INCIDENT: '${incident.id}', date: '${incident.date}', " +
                "service: '${incident.service.id}/${incident.service.name}:${incident.service.version}', " +
                "details: [$detailsAsText]"
        }

        logger.info(message = incident)
        throw PvmException(incident)
    }
}
