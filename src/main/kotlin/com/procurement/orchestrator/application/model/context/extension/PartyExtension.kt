package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.GlobalContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.model.party.Party

private const val NAME = "parties"

fun GlobalContext.getPartyIfOnlyOne(): Result<Party, Fail.Incident.Bpms.Context> =
    this.parties.getElementIfOnlyOne(name = NAME)
