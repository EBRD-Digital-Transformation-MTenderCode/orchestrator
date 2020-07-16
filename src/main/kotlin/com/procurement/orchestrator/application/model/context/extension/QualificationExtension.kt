package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse

private const val PATH = "qualifications"

private const val REQUIREMENT_RESPONSE_NAME = "requirementResponses"
private const val DOCUMENTS_NAME = "documents"

fun GlobalContext.getQualificationIfOnlyOne(): Result<Qualification, Fail.Incident.Bpms.Context> =
    this.qualifications.getElementIfOnlyOne(name = PATH)

fun Qualification.getRequirementResponseIfOnlyOne(): Result<RequirementResponse, Fail.Incident.Bpms.Context> =
    this.requirementResponses.getElementIfOnlyOne(name = REQUIREMENT_RESPONSE_NAME, path = PATH)

fun GlobalContext.getQualificationsIfNotEmpty(): Result<List<Qualification>, Fail.Incident.Bpms.Context> =
    this.qualifications.getIfNotEmpty(name = PATH)

fun Qualification.getDocumentsIfNotEmpty(): Result<List<Document>, Fail.Incident.Bpms.Context> =
    this.documents.getIfNotEmpty(name = DOCUMENTS_NAME, path = PATH)
