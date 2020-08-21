package com.procurement.orchestrator.application.repository

import com.procurement.orchestrator.application.model.Phase
import com.procurement.orchestrator.application.model.Rule
import com.procurement.orchestrator.application.model.Stage
import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId

interface RuleRepository {
    fun load(
        countryId: CountryId,
        pmd: ProcurementMethodDetails,
        processDefinitionKey: ProcessDefinitionKey
    ): Result<List<Rule>, Fail.Incident.Database>

    fun load(
        countryId: CountryId,
        pmd: ProcurementMethodDetails,
        processDefinitionKey: ProcessDefinitionKey,
        stageFrom: Stage?,
        phaseFrom: Phase?
    ): Result<Rule?, Fail.Incident.Database>
}
