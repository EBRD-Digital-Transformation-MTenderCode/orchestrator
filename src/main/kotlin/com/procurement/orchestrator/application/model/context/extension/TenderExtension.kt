package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.tender.Tender

private const val PATH = "tender.amendments"

fun Tender.getAmendmentsIfNotEmpty(): Result<List<Amendment>, Fail.Incident.Bpmn.Context> =
    this.amendments.getElementIfNotEmpty(path = PATH)
