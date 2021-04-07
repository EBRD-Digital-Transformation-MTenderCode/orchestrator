package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.context.members.ProcessInfo
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.application.model.process.AdditionalProcess
import com.procurement.orchestrator.domain.model.item.Items
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.lot.LotId
import com.procurement.orchestrator.domain.model.lot.Lots
import com.procurement.orchestrator.domain.model.tender.Tender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class TransferRfqDataDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_TENDER = "tender"
        private const val VARIABLE_PROCESS_INFO = "processInfo"
        private const val VARIABLE_REQUEST_INFO = "requestInfo"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val processInfo = superExecution.getVariable(VARIABLE_PROCESS_INFO) as ProcessInfo

        val updatedProcessInfo = getUpdatedProcessInfo(processInfo, processInfo.additionalProcess!!)
        val updatedRequestInfo = getUpdatedRequestInfo(superExecution)

        val tender = superExecution.getVariable(VARIABLE_TENDER) as Tender
        val subTender = Tender(
            items = Items(tender.items),
            lots = Lots(
                Lot(id = LotId.create(processInfo.entityId!!))
            )
        )

        subVariables.putValue(VARIABLE_PROCESS_INFO, updatedProcessInfo)
        subVariables.putValue(VARIABLE_REQUEST_INFO, updatedRequestInfo)
        subVariables.putValue(VARIABLE_TENDER, subTender)
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
        additionalProcess: AdditionalProcess
    ): ProcessInfo {
        val processCpid = processInfo.cpid!!
        val processOcid = processInfo.ocid
        val additionalProcessCpid = additionalProcess.cpid
        val additionalProcessOcid = additionalProcess.ocid

        return processInfo.copy(
            cpid = additionalProcessCpid,
            ocid = additionalProcessOcid,
            additionalProcess = additionalProcess.copy(
                cpid = processCpid,
                ocid = processOcid
            )
        )
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {

    }
}
