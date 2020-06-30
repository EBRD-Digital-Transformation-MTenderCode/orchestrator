package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.organization.person.BusinessFunction

private const val AWARD_BUSINESS_FUNCTION_PATH = "awards.requirementResponses.responder.businessFunctions"
private const val QUALIFICATION_BUSINESS_FUNCTIONS_PATH = "qualifications.requirementResponses.responder.businessFunctions"
private const val SUBMISSIONS_BUSINESS_FUNCTIONS_PATH = "submissions.details.candidates.persones.businessFunctions"

fun BusinessFunction.getAwardDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    getDocumentsIfNotEmpty(path = AWARD_BUSINESS_FUNCTION_PATH)

fun BusinessFunction.getQualificationDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    getDocumentsIfNotEmpty(path = QUALIFICATION_BUSINESS_FUNCTIONS_PATH)

fun BusinessFunction.getSubmissionDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    getDocumentsIfNotEmpty(path = SUBMISSIONS_BUSINESS_FUNCTIONS_PATH)

fun BusinessFunction.getDocumentsIfNotEmpty(path: String): Result<List<Document>, Fail.Incident.Bpms.Context> =
    this.documents.getIfNotEmpty(name = "documents", path = path)
