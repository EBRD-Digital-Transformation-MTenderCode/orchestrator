package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.person.Person

fun Person.getBusinessFunctionsIfNotEmpty(path: String): Result<List<BusinessFunction>, Fail.Incident.Bpms.Context> =
    this.businessFunctions.getIfNotEmpty(name = "businessFunctions", path = path)
