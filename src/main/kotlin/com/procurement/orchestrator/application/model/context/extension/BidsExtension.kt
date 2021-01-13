package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.bid.Bid
import com.procurement.orchestrator.domain.model.bid.Bids
import com.procurement.orchestrator.domain.model.bid.BidsDetails

private const val PATH = "#/bids"

fun Bids?.getBidsDetailIfOnlyOne(): Result<Bid, Fail.Incident.Bpms.Context> =
    this?.details
        ?.getElementIfOnlyOne(name = PATH)
        ?: Result.failure(Fail.Incident.Bpms.Context.Missing(name = "bids"))

fun Bids?.getBidsDetailIfNotEmpty(): Result<BidsDetails, Fail.Incident.Bpms.Context> =
    this?.details
        ?.getIfNotEmpty(name = PATH)
        ?: Result.failure(Fail.Incident.Bpms.Context.Missing(name = "details"))

fun GlobalContext.tryGetBids(): Result<Bids, Fail.Incident.Bpms.Context> =
    this.bids
        ?.asSuccess()
        ?: Result.failure(Fail.Incident.Bpms.Context.Missing(name = "bids"))
