package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.domain.model.contract.Contract
import com.procurement.orchestrator.domain.model.contract.Contracts
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class TransferContractDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_CONTRACT = "contract"
        private const val VARIABLE_CONTRACTS = "contracts"
        private const val VARIABLE_PROCESS_INFO = "processInfo"
        private const val VARIABLE_REQUEST_INFO = "requestInfo"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val processInfo = superExecution.getVariable(VARIABLE_PROCESS_INFO) as ProcessInfo
        val updatedRequestInfo = getUpdatedRequestInfo(superExecution)
        val contract = superExecution.getVariable(VARIABLE_CONTRACT) as Contract

        subVariables.putValue(VARIABLE_PROCESS_INFO, processInfo)
        subVariables.putValue(VARIABLE_REQUEST_INFO, updatedRequestInfo)
        subVariables.putValue(VARIABLE_CONTRACTS, Contracts(contract))
    }

    private fun getUpdatedRequestInfo(superExecution: DelegateExecution): RequestInfo {
        val requestInfo = superExecution.getVariable(VARIABLE_REQUEST_INFO) as RequestInfo
        return requestInfo.copy(
            parentOperationId = requestInfo.operationId,
            operationId = OperationId.generate()
        )
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {
        // Nothing to map
    }
}
