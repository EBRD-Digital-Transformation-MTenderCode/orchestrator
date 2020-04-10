package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.domain.model.tender.Tender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class CompleteRecordMappingContextDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_NAME = "tender"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val tender = superExecution.getVariable(VARIABLE_NAME) as Tender
        val subTender = Tender(
            status = tender.status,
            statusDetails = tender.statusDetails,
            awardPeriod = tender.awardPeriod
        )
        subVariables.putValue(VARIABLE_NAME, subTender)
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {
        val tender: Tender = superExecution.getVariable(VARIABLE_NAME) as Tender
        val subTender: Tender = subInstance.getVariable(VARIABLE_NAME) as Tender
        val updatedTender = tender.copy(
            status = subTender.status,
            statusDetails = subTender.statusDetails,
            awardPeriod = subTender.awardPeriod
        )
        superExecution.setVariable(VARIABLE_NAME, updatedTender)
    }
}
