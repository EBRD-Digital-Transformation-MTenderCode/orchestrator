package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.container.PropertyContainer
import com.procurement.orchestrator.application.model.context.serialize
import com.procurement.orchestrator.application.repository.ProcessInitializerRepository
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.extension.date.nowDefaultUTC
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStep
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.pvm.PvmException
import org.springframework.stereotype.Component

@Component
class BpeProcessInitializerDelegate(
    private val logger: Logger,
    private val transform: Transform,
    private val operationStepRepository: OperationStepRepository,
    private val processInitializerRepository: ProcessInitializerRepository
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val context = CamundaGlobalContext(propertyContainer = propertyContainer(execution))
        val requestInfo = context.requestInfo
        val processInfo = context.processInfo
        val processId = execution.processInstanceId
        val taskId = execution.currentActivityId
        val stepDate = nowDefaultUTC()

        val serializedContext = context.serialize(transform)
            .orReturnFail { fail -> fail.throwBpmnIncident() }

        operationStepRepository
            .save(
                step = OperationStep(
                    cpid = processInfo.cpid!!,
                    operationId = requestInfo.operationId,
                    processId = processId,
                    taskId = taskId,
                    stepDate = stepDate,
                    request = "",
                    response = "",
                    context = serializedContext
                )
            )
            .doOnError { fail -> fail.throwBpmnIncident() }

        val launchedProcessInfo = processInitializerRepository
            .launchProcess(
                operationId = requestInfo.operationId,
                timestamp = nowDefaultUTC(),
                processId = execution.processInstanceId,
                cpid = processInfo.cpid
            )
            .orReturnFail { fail -> fail.throwBpmnIncident() }

        if (!launchedProcessInfo.wasLaunched && launchedProcessInfo.processId != execution.processInstanceId) {
            throw BpmnError("Attention starting duplicate process.") //TODO BpmnCodeError
        }
    }

    private fun propertyContainer(execution: DelegateExecution) = object :
        PropertyContainer {
        override fun get(name: String): Any? = execution.getVariable(name)

        override fun set(name: String, value: Any) {
            execution.setVariable(name, value)
        }
    }

    private fun Fail.Incident.throwBpmnIncident(): Nothing {
        logging(logger)
        throw PvmException("${this.level.name} INCIDENT: '${this.description}'")
    }
}
