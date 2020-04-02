package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.amendment.Amendment
import com.procurement.orchestrator.domain.model.lot.Lot
import com.procurement.orchestrator.domain.model.tender.Tender

private const val PATH = "tender"

fun Tender.getAmendmentsIfNotEmpty(): Result<List<Amendment>, Fail.Incident.Bpms.Context> =
    this.amendments.getIfNotEmpty(name = "amendments", path = PATH)

fun Tender.getLotIfOnlyOne(): Result<Lot, Fail.Incident.Bpms.Context> =
    this.lots.getElementIfOnlyOne(name = "lots", path = PATH)
