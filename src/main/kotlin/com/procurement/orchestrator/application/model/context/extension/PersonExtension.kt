package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction
import com.procurement.orchestrator.domain.model.person.Person

private const val QUALIFICATION_RESPONDER_PATH = "qualifications.requirementResponses.responder"
private const val AWARD_RESPONDER_PATH = "awards.requirementResponses.responder"
private const val SUBMISSIONS_PERSONES_PATH = "submissions.details.candidates.persones"

fun Person.getQualificationBusinessFunctionsIfNotEmpty(): Result<List<BusinessFunction>, Fail.Incident.Bpms.Context> =
    getBusinessFunctionsIfNotEmpty(path = QUALIFICATION_RESPONDER_PATH)

fun Person.getAwardBusinessFunctionsIfNotEmpty(): Result<List<BusinessFunction>, Fail.Incident.Bpms.Context> =
    getBusinessFunctionsIfNotEmpty(path = AWARD_RESPONDER_PATH)

fun Person.getSubmissionBusinessFunctionsIfNotEmpty(): Result<List<BusinessFunction>, Fail.Incident.Bpms.Context> =
    getBusinessFunctionsIfNotEmpty(path = SUBMISSIONS_PERSONES_PATH)

fun Person.getBusinessFunctionsIfNotEmpty(path: String): Result<List<BusinessFunction>, Fail.Incident.Bpms.Context> =
    this.businessFunctions.getIfNotEmpty(name = "businessFunctions", path = path)
