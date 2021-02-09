package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.domain.model.party.Parties
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class TransferPartiesToOtherProcessDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_PARTIES = "parties"
        private const val VARIABLE_PROCESS_INFO = "processInfo"
        private const val VARIABLE_REQUEST_INFO = "requestInfo"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val processInfo = superExecution.getVariable(VARIABLE_PROCESS_INFO) as ProcessInfo

        val updatedProcessInfo = getUpdatedProcessInfo(processInfo, processInfo.relatedProcess!!)
        val updatedRequestInfo = getUpdatedRequestInfo(superExecution)

        val parties = superExecution.getVariable(VARIABLE_PARTIES) as Parties

        subVariables.putValue(VARIABLE_PROCESS_INFO, updatedProcessInfo)
        subVariables.putValue(VARIABLE_REQUEST_INFO, updatedRequestInfo)
        subVariables.putValue(VARIABLE_PARTIES, parties)
    }

    private fun getUpdatedRequestInfo(superExecution: DelegateExecution): RequestInfo {
        val requestInfo = superExecution.getVariable(VARIABLE_REQUEST_INFO) as RequestInfo
        val updatedRequestInfo = requestInfo.copy(
            parentOperationId = requestInfo.operationId,
            operationId = OperationId.generate()
        )
        return updatedRequestInfo
    }

    private fun getUpdatedProcessInfo(
        processInfo: ProcessInfo,
        relatedProcess: ProcessInfo.RelatedProcess
    ): ProcessInfo {
        val processCpid = processInfo.cpid!!
        val processOcid = processInfo.ocid!!
        val relatedProcessCpid = relatedProcess.cpid
        val relatedProcessOcid = relatedProcess.ocid

        return processInfo.copy(
            cpid = relatedProcessCpid,
            ocid = relatedProcessOcid,
            relatedProcess = relatedProcess.copy(
                cpid = processCpid,
                ocid = processOcid
            )
        )
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {
    }
}
