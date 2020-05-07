package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.organization.Organization
import com.procurement.orchestrator.domain.model.person.Person

private const val NAME_DETAILS = "persones"

fun Organization.getPersonesIfNotEmpty(): Result<List<Person>, Fail.Incident.Bpms.Context> =
    this.persons.getIfNotEmpty(name = NAME_DETAILS)


