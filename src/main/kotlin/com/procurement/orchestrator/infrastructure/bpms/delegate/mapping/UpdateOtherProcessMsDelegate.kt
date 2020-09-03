package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.domain.model.Ocid
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class UpdateOtherProcessMsDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_PROCESS_INFO = "processInfo"
        private const val VARIABLE_REQUEST_INFO = "requestInfo"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val processInfo = superExecution.getVariable(VARIABLE_PROCESS_INFO) as ProcessInfo
        val requestInfo = superExecution.getVariable(VARIABLE_REQUEST_INFO) as RequestInfo

        val relatedProcess = processInfo.relatedProcess!!

        val updatedProcessInfo = processInfo.copy(
            relatedProcess = relatedProcess.copy(cpid = processInfo.cpid),
            cpid = relatedProcess.cpid,
            ocid = Ocid.parse(relatedProcess.cpid.toString())!!
        )

        val updatedRequestInfo = requestInfo.copy(
            parentOperationId = requestInfo.operationId,
            operationId = OperationId.generate()
        )

        subVariables.putValue(VARIABLE_PROCESS_INFO, updatedProcessInfo)
        subVariables.putValue(VARIABLE_REQUEST_INFO, updatedRequestInfo)
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {
    }
}
