package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId

private const val PATH = "awards.requirementResponses"

fun List<RequirementResponse>.getRequirementResponseIfOnlyOne(): Result<RequirementResponse, Fail.Incident.Bpmn.Context> =
    this.getElementIfOnlyOne(path = PATH)

fun List<RequirementResponse>.findRequirementResponseById(
    id: RequirementResponseId
): Result<RequirementResponse, Fail.Incident.Bpmn.Context.NotFoundElement> = this
    .find { it.id == id }
    ?.asSuccess()
    ?: Result.failure(Fail.Incident.Bpmn.Context.NotFoundElement(id = id.toString(), path = PATH))
