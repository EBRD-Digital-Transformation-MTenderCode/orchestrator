package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.qualification.Qualification
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse

private const val PATH = "qualifications"

private const val REQUIREMENT_RESPONSE_NAME = "requirementResponses"

fun GlobalContext.getQualificationIfOnlyOne(): Result<Qualification, Fail.Incident.Bpms.Context> =
    this.qualifications.getElementIfOnlyOne(name = PATH)

fun Qualification.getRequirementResponseIfOnlyOne(): Result<RequirementResponse, Fail.Incident.Bpms.Context> =
    this.requirementResponses.getElementIfOnlyOne(name = REQUIREMENT_RESPONSE_NAME, path = PATH)