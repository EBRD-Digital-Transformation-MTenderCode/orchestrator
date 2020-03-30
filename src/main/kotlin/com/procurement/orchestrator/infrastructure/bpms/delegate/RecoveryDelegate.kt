package com.procurement.orchestrator.infrastructure.bpms.delegate

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class RecoveryDelegate : JavaDelegate {
    companion object {
        const val FAILED_ACTIVITY_ID = "errorTaskId"
    }

    override fun execute(execution: DelegateExecution) {
        val errorTaskId = execution.getVariable(FAILED_ACTIVITY_ID)

        execution.processEngineServices.runtimeService
            .createProcessInstanceModification(execution.processInstanceId)
            .startBeforeActivity(errorTaskId.toString())
            .execute()
    }
}
