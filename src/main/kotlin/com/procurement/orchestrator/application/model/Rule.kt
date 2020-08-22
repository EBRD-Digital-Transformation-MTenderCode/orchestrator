package com.procurement.orchestrator.application.model

import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId

class Rule(
    var countryId: CountryId,
    val pmd: ProcurementMethodDetails,
    val processDefinitionKey: ProcessDefinitionKey,
    val stageFrom: Stage?,
    val stageTo: Stage,
    val phaseFrom: Phase?,
    val phaseTo: Phase,
    val operationType: OperationTypeProcess
)
