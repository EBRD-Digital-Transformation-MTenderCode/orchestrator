package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponse
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId

private const val PATH = "awards.requirementResponses"

fun Award.getRequirementResponseIfOnlyOne(): Result<RequirementResponse, Fail.Incident.Bpmn.Context> =
    this.requirementResponses.getElementIfOnlyOne(path = PATH)

fun Award.findRequirementResponseById(
    id: RequirementResponseId
): Result<RequirementResponse, Fail.Incident.Bpmn.Context.NotFoundElement> = this.requirementResponses
    .find { it.id == id }
    ?.asSuccess()
    ?: Result.failure(Fail.Incident.Bpmn.Context.NotFoundElement(id = id.toString(), path = PATH))
