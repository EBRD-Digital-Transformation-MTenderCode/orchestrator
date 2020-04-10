package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.tender.Tender
import com.procurement.orchestrator.domain.util.extension.getNewElements
import com.procurement.orchestrator.domain.util.extension.toSetBy
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class CancelLotMappingContextDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_NAME = "tender"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val tender = superExecution.getVariable(VARIABLE_NAME) as Tender
        val subTender = Tender(lots = tender.lots)
        subVariables.putValue(VARIABLE_NAME, subTender)
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {
        val tender: Tender = superExecution.getVariable(VARIABLE_NAME) as Tender
        val subTender: Tender = subInstance.getVariable(VARIABLE_NAME) as Tender
        val subLotsById = subTender.lots.associateBy { it.id }
        val updatedLots: List<Lot> = tender.lots
            .map { lot ->
                subLotsById[lot.id]
                    ?.let { subLot -> lot.update(subLot) }
                    ?: lot
            }

        val existLotIds = tender.lots.toSetBy { it.id }
        val subLotsIds = subLotsById.keys
        val newLots: List<Lot> = getNewElements(received = subLotsIds, known = existLotIds)
            .map { id -> subLotsById.getValue(id).create() }

        val updatedTender = tender.copy(lots = updatedLots + newLots)
        superExecution.setVariable(VARIABLE_NAME, updatedTender)
    }

    private fun Lot.create(): Lot = Lot(
        id = id,
        status = status,
        statusDetails = statusDetails
    )

    private fun Lot.update(lot: Lot): Lot = this.copy(
        status = lot.status,
        statusDetails = lot.statusDetails
    )
}
