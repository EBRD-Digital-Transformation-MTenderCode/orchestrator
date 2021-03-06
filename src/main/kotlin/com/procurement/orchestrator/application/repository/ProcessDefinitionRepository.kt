package com.procurement.orchestrator.application.repository

import com.procurement.orchestrator.application.model.process.ProcessDefinitionKey
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.ProcurementMethodDetails
import com.procurement.orchestrator.domain.model.address.country.CountryId

interface ProcessDefinitionRepository {

    fun getProcessDefinitionKey(
        countryId: CountryId,
        pmd: ProcurementMethodDetails,
        processName: String
    ): Result<ProcessDefinitionKey?, Fail.Incident.Database.Access>
}
