package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.award.Award
import com.procurement.orchestrator.domain.model.award.AwardId

private const val PATH = "awards"

fun GlobalContext.getAwardIfOnlyOne(): Result<Award, Fail.Incident.Bpmn.Context> =
    this.awards.getElementIfOnlyOne(path = PATH)

fun GlobalContext.getAwardsIfNotEmpty(): Result<List<Award>, Fail.Incident.Bpmn.Context> =
    this.awards.getIfNotEmpty(path = PATH)

fun GlobalContext.findAwardById(id: AwardId): Result<Award, Fail.Incident.Bpmn.Context.NotFoundElement> = this.awards
    .find { it.id == id }
    ?.asSuccess()
    ?: failure(Fail.Incident.Bpmn.Context.NotFoundElement(id = id.toString(), path = PATH))
