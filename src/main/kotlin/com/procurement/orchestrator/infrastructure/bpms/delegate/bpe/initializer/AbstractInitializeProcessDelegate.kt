package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe.initializer

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.members.Errors
import com.procurement.orchestrator.application.model.context.members.Incident
import com.procurement.orchestrator.application.model.context.serialize
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.format
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.fail.error.DataValidationErrors
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.infrastructure.bpms.extension.readErrors
import com.procurement.orchestrator.infrastructure.bpms.extension.writeErrors
import com.procurement.orchestrator.infrastructure.bpms.extension.writeIncident
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.configuration.property.GlobalProperties
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
abstract class AbstractInitializeProcessDelegate(
    private val logger: Logger,
    private val transform: Transform,
    private val operationStepRepository: OperationStepRepository,
    private val processInitializerRepository: ProcessInitializerRepository
) : JavaDelegate {

    companion object {
        private const val VALIDATION_ERROR_CODE = "ValidationError"
        private const val INTERNAL_INCIDENT_CODE = "InternalIncident"
    }

    final override fun execute(execution: DelegateExecution) {
        val camundaContext = CamundaContext(propertyContainer = propertyContainer(execution))
        val globalContext = CamundaGlobalContext(propertyContainer = propertyContainer(execution))
        updateGlobalContext(camundaContext, globalContext)
            .doOnFail { fail ->
                when (fail) {
                    is Fail.Error -> throwError(execution = execution, error = fail)

                    is Fail.Incident -> when (fail) {
                        is Fail.Incident.Transform -> throwTransformError(execution = execution, incident = fail)
                        else -> throwIncident(execution = execution, incident = fail)
                    }
                }
            }

        val serializedContext = globalContext.serialize(transform)
            .doOnError { fail -> throwIncident(execution = execution, incident = fail) }
            .get
        val requestInfo = globalContext.requestInfo
        val processInfo = globalContext.processInfo
        val timestamp = nowDefaultUTC()
        operationStepRepository
            .save(
                step = OperationStep(
                    cpid = processInfo.cpid,
                    operationId = requestInfo.operationId,
                    processId = execution.processInstanceId,
                    taskId = execution.currentActivityId,
                    stepDate = timestamp,
                    request = "",
                    response = "",
                    context = serializedContext
                )
            )
            .doOnError { fail -> throwIncident(execution = execution, incident = fail) }

        val launchedProcessInfo = processInitializerRepository
            .launchProcess(
                operationId = requestInfo.operationId,
                timestamp = timestamp,
                processId = execution.processInstanceId,
                cpid = processInfo.cpid
            )
            .orReturnFail { fail -> throwIncident(execution = execution, incident = fail) }

        if (!launchedProcessInfo.wasLaunched && launchedProcessInfo.processId != execution.processInstanceId) {
            throw BpmnError("Attention starting duplicate process.") //TODO BpmnCodeError
        }
    }

    protected abstract fun updateGlobalContext(
        camundaContext: CamundaContext,
        globalContext: CamundaGlobalContext
    ): MaybeFail<Fail>

    private fun propertyContainer(execution: DelegateExecution) = object : PropertyContainer {
        override fun get(name: String): Any? = execution.getVariable(name)

        override fun set(name: String, value: Any) {
            execution.setVariable(name, value)
        }
    }

    private fun throwIncident(execution: DelegateExecution, incident: Fail.Incident): Nothing {
        incident.logging(logger)
        execution.writeIncident(
            Incident(
                id = execution.processInstanceId,
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
        throw BpmnError(INTERNAL_INCIDENT_CODE, execution.currentActivityId)
    }

    private fun throwTransformError(execution: DelegateExecution, incident: Fail.Incident.Transform): Nothing {
        incident.logging(logger)
        val newErrors = listOf(
            Errors.Error(
                code = incident.code,
                description = incident.description,
                details = listOf()
            )
        )
        execution.writeErrors(execution.readErrors() + newErrors)
        throw BpmnError(VALIDATION_ERROR_CODE)
    }

    private fun throwError(execution: DelegateExecution, error: Fail.Error): Nothing {
        error.logging(logger)
        val newErrors = listOf(
            Errors.Error(
                code = error.code,
                description = error.description,
                details = when (error) {
                    is DataValidationErrors -> listOf(Errors.Error.Detail(name = error.name))
                    else -> emptyList()
                }
            )
        )
        execution.writeErrors(execution.readErrors() + newErrors)
        throw BpmnError(VALIDATION_ERROR_CODE)
    }
}
